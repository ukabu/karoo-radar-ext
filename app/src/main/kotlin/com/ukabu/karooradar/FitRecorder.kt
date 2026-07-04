package com.ukabu.karooradar

import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.DeveloperField
import io.hammerhead.karooext.models.FieldValue
import io.hammerhead.karooext.models.FitEffect
import io.hammerhead.karooext.models.RideState
import io.hammerhead.karooext.models.WriteToRecordMesg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Writes radar developer fields to the FIT file at ~1 Hz during active recording.
 */
class FitRecorder(
    private val radarStateFlow: StateFlow<RadarState>,
    private val rideStateFlow: Flow<RideState>,
) {

    private val vehiclesField = DeveloperField(
        fieldDefinitionNumber = 0,
        fitBaseTypeId = 2, // FitBaseType.UINT8
        fieldName = "radar_vehicles",
        units = "count",
    )

    private val distanceField = DeveloperField(
        fieldDefinitionNumber = 1,
        fitBaseTypeId = 132, // FitBaseType.UINT16
        fieldName = "radar_nearest_distance_m",
        units = "m",
    )

    private val relSpeedField = DeveloperField(
        fieldDefinitionNumber = 2,
        fitBaseTypeId = 2, // FitBaseType.UINT8
        fieldName = "radar_relative_speed_kmh",
        units = "km/h",
    )

    private val absSpeedField = DeveloperField(
        fieldDefinitionNumber = 3,
        fitBaseTypeId = 2, // FitBaseType.UINT8
        fieldName = "radar_absolute_speed_kmh",
        units = "km/h",
    )

    private var fitJob: Job? = null

    fun start(emitter: Emitter<FitEffect>) {
        fitJob = CoroutineScope(Dispatchers.IO).launch {
            combine(
                radarStateFlow,
                rideStateFlow,
            ) { radar, ride -> radar to ride }
                .collect { (radar, ride) ->
                    if (ride !is RideState.Recording || !radar.isConnected) {
                        return@collect
                    }

                    val values = listOf(
                        FieldValue(vehiclesField, radar.vehicleCount.toDouble()),
                        FieldValue(distanceField, radar.closestDistanceMeters ?: 0.0),
                        FieldValue(relSpeedField, radar.relativeSpeedKmh ?: 0.0),
                        FieldValue(absSpeedField, radar.absoluteSpeedKmh ?: 0.0),
                    )

                    emitter.onNext(WriteToRecordMesg(values))
                }
        }
    }

    fun stop() {
        fitJob?.cancel()
        fitJob = null
    }
}
