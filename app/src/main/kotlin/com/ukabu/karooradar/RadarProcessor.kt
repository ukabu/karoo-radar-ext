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
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

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

    companion object {
        private const val HANDOFF_SAMPLES = 2
        private const val EMA_ALPHA = 0.3
        private const val MAX_RELATIVE_SPEED_KMH = 200.0
        private const val SPEED_WINDOW_MS = 3000L
        private const val MIN_SPEED_DT_MS = 1000L
    }

    private data class DistanceSample(val time: Long, val distance: Double)

    // EMA state for relative speed derivation
    private var emaSpeed: Double = 0.0
    private val distanceHistory = ArrayDeque<DistanceSample>()
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
        val nonZeroRanges = ranges.mapIndexedNotNull { idx, v ->
            if (v > 0.0) idx to v else null
        }

        val vehicleCount = nonZeroRanges.size

        if (vehicleCount == 0) {
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
            resetEma()
            return
        }

        val (closestSlot, closestDist) = nonZeroRanges.minByOrNull { it.second }!!
        val threatLevel = ThreatLevel.fromInt(
            (dp.values[DataType.Field.RADAR_THREAT_LEVEL] ?: 0.0).toInt()
        )

        // Handoff detection: when the closest target slot changes, suppress speed
        // display for a short transition period.
        val prevSlot = _radarState.value.closestSlotIndex
        if (prevSlot != -1 && prevSlot != closestSlot) {
            handoffRemainingSamples = HANDOFF_SAMPLES
        }

        // DataPoint does not expose a sample timestamp, so we fall back to wall-clock
        // time. This means scheduling jitter can affect the derived dt.
        val timestampMs = System.currentTimeMillis()
        val relativeSpeedKmh = deriveRelativeSpeed(closestDist, timestampMs)

        val cyclistSpeedKmh = _speedState.value.speedKmh
        val absoluteSpeedKmh = if (relativeSpeedKmh != null && cyclistSpeedKmh != null) {
            relativeSpeedKmh + cyclistSpeedKmh
        } else {
            null
        }

        _radarState.value = RadarState(
            vehicleCount = vehicleCount,
            closestDistanceMeters = closestDist,
            threatLevel = threatLevel,
            relativeSpeedKmh = relativeSpeedKmh,
            absoluteSpeedKmh = absoluteSpeedKmh,
            isConnected = true,
            closestSlotIndex = closestSlot,
            isHandoffActive = handoffRemainingSamples > 0,
        )
    }

    private fun deriveRelativeSpeed(currentDistance: Double, timestampMs: Long): Double? {
        distanceHistory.addLast(DistanceSample(timestampMs, currentDistance))

        // During a handoff we keep collecting samples but suppress the computed
        // speed so the UI shows "--" for a short transition period.
        if (handoffRemainingSamples > 0) {
            handoffRemainingSamples--
            return null
        }

        // Discard samples outside the sliding window
        while (distanceHistory.isNotEmpty() && timestampMs - distanceHistory.first().time > SPEED_WINDOW_MS) {
            distanceHistory.removeFirst()
        }

        if (distanceHistory.size < 2) {
            return emaSpeed.takeIf { it > 0 }
        }

        val oldest = distanceHistory.first()
        val newest = distanceHistory.last()
        val dtMs = newest.time - oldest.time
        if (dtMs < MIN_SPEED_DT_MS) {
            return emaSpeed.takeIf { it > 0 }
        }

        val dD = newest.distance - oldest.distance
        val dtSeconds = dtMs / 1000.0
        val instantaneousKmh = (-dD / dtSeconds) * 3.6 // m/s to km/h
        val clamped = max(0.0, min(instantaneousKmh, MAX_RELATIVE_SPEED_KMH))

        emaSpeed = if (emaSpeed == 0.0) clamped else {
            EMA_ALPHA * clamped + (1 - EMA_ALPHA) * emaSpeed
        }

        return emaSpeed
    }

    private fun resetEma() {
        emaSpeed = 0.0
        distanceHistory.clear()
    }
}
