package com.ukabu.karooradar

import android.content.Context
import com.ukabu.karooradar.R
import com.ukabu.karooradar.RadarExtension
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.ViewConfig

/**
 * Displays the total number of vehicles detected by the radar.
 */
class VehicleCountDataType(
    private val radarExtension: RadarExtension,
) : DataTypeImpl("karoo-radar", "vehicle-count") {

    override fun startStream(emitter: Emitter<StreamState>) {
        DatafieldUtils.startDummyStream(emitter, dataTypeId)
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        DatafieldUtils.startRadarView(
            context = context,
            emitter = emitter,
            radarExtension = radarExtension,
            labelRes = R.string.vehicle_count,
            valueProvider = { state, _ ->
                state.vehicleCount.toString()
            }
        )
    }
}
