package com.ukabu.karooradar

import android.content.Context
import com.ukabu.karooradar.R
import com.ukabu.karooradar.RadarExtension
import com.ukabu.karooradar.Units
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.ViewConfig

/**
 * Displays distance to the closest detected vehicle.
 */
class ClosestDistanceDataType(
    private val radarExtension: RadarExtension,
) : DataTypeImpl("karoo-radar", "closest-distance") {

    override fun startStream(emitter: Emitter<StreamState>) {
        DatafieldUtils.startDummyStream(emitter, dataTypeId)
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        DatafieldUtils.startRadarView(
            context = context,
            emitter = emitter,
            radarExtension = radarExtension,
            labelRes = R.string.closest_distance,
            valueProvider = { state, isImperial ->
                val dist = state.closestDistanceMeters
                if (dist == null) "--" else {
                    if (isImperial) {
                        "${Units.metersToFeet(dist).toInt()}ft"
                    } else {
                        "${dist.toInt()}m"
                    }
                }
            }
        )
    }
}
