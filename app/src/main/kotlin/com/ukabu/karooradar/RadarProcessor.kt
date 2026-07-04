package com.ukabu.karooradar

import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Consumes the Karoo RADAR data stream, computes derived metrics, and exposes
 * a [StateFlow<RadarState>] for datafields and FIT recording.
 */
class RadarProcessor(
    private val karooSystem: KarooSystemService,
) {
    private val _radarState = MutableStateFlow(RadarState())
    val radarState: StateFlow<RadarState> = _radarState.asStateFlow()

    private val _speedState = MutableStateFlow(SpeedState())
    val speedState: StateFlow<SpeedState> = _speedState.asStateFlow()

    private val targetRangeFields = listOf(
        DataType.Field.RADAR_TARGET_1_RANGE,
        DataType.Field.RADAR_TARGET_2_RANGE,
        DataType.Field.RADAR_TARGET_3_RANGE,
        DataType.Field.RADAR_TARGET_4_RANGE,
        DataType.Field.RADAR_TARGET_5_RANGE,
        DataType.Field.RADAR_TARGET_6_RANGE,
        DataType.Field.RADAR_TARGET_7_RANGE,
        DataType.Field.RADAR_TARGET_8_RANGE,
    )

    private val speedCalculator = RelativeSpeedCalculator()
    private var handoffRemainingSamples: Int = 0

    private var processorJob: Job? = null

    fun start() {
        processorJob = CoroutineScope(Dispatchers.IO).launch {
            launch { subscribeRadar() }
            launch { subscribeSpeed() }
        }
    }

    fun stop() {
        processorJob?.cancel()
        processorJob = null
    }

    private suspend fun subscribeRadar() {
        karooSystem.streamDataFlow(DataType.Type.RADAR).collect { state ->
            when (state) {
                is StreamState.Streaming -> processRadarDataPoint(state.dataPoint)
                is StreamState.NotAvailable,
                is StreamState.Searching,
                is StreamState.Idle -> {
                    _radarState.value = RadarState(isConnected = false)
                }
            }
        }
    }

    private suspend fun subscribeSpeed() {
        karooSystem.streamDataFlow(DataType.Type.SPEED).collect { state ->
            when (state) {
                is StreamState.Streaming -> {
                    val speedMps = state.dataPoint.values[DataType.Field.SPEED]
                    _speedState.value = SpeedState(speedMps = speedMps)
                }
                else -> {
                    _speedState.value = SpeedState()
                }
            }
        }
    }

    private fun processRadarDataPoint(dp: io.hammerhead.karooext.models.DataPoint) {
        val ranges = targetRangeFields.map { dp.values[it] ?: 0.0 }
        val vehicleState = computeVehicleState(
            ranges = ranges,
            threatValue = (dp.values[DataType.Field.RADAR_THREAT_LEVEL] ?: 0.0).toInt(),
        )

        if (vehicleState.vehicleCount == 0) {
            _radarState.value = RadarState(
                vehicleCount = 0,
                closestDistanceMeters = null,
                threatLevel = ThreatLevel.CLEAR,
                relativeSpeedKmh = null,
                absoluteSpeedKmh = null,
                isConnected = true,
                closestSlotIndex = -1,
                isHandoffActive = false,
            )
            speedCalculator.reset()
            handoffRemainingSamples = 0
            return
        }

        // Handoff detection: when the closest target slot changes, suppress speed
        // display for a short transition period.
        val prevSlot = _radarState.value.closestSlotIndex
        if (prevSlot != -1 && prevSlot != vehicleState.closestSlotIndex) {
            handoffRemainingSamples = RelativeSpeedCalculator.DEFAULT_HANDOFF_SAMPLES
        }

        // DataPoint does not expose a sample timestamp, so we fall back to wall-clock
        // time. This means scheduling jitter can affect the derived dt.
        val timestampMs = System.currentTimeMillis()
        val (relativeSpeedKmh, remainingHandoff) = speedCalculator.calculate(
            currentDistance = vehicleState.closestDistanceMeters!!,
            timestampMs = timestampMs,
            handoffRemainingSamples = handoffRemainingSamples,
        )
        handoffRemainingSamples = remainingHandoff

        val absoluteSpeedKmh = deriveAbsoluteSpeed(
            relativeSpeedKmh = relativeSpeedKmh,
            cyclistSpeedKmh = _speedState.value.speedKmh,
        )

        _radarState.value = RadarState(
            vehicleCount = vehicleState.vehicleCount,
            closestDistanceMeters = vehicleState.closestDistanceMeters,
            threatLevel = vehicleState.threatLevel,
            relativeSpeedKmh = relativeSpeedKmh,
            absoluteSpeedKmh = absoluteSpeedKmh,
            isConnected = true,
            closestSlotIndex = vehicleState.closestSlotIndex,
            isHandoffActive = handoffRemainingSamples > 0,
        )
    }
}
