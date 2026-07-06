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
 * Displays derived relative speed of the closest vehicle (approaching speed).
 */
class RelativeSpeedDataType(
    private val radarExtension: RadarExtension,
) : DataTypeImpl("karoo-radar", "relative-speed") {

    override fun startStream(emitter: Emitter<StreamState>) {
        DatafieldUtils.startDummyStream(emitter, dataTypeId)
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        DatafieldUtils.startRadarView(
            context = context,
            config = config,
            emitter = emitter,
            radarExtension = radarExtension,
            labelRes = R.string.relative_speed,
            valueProvider = { state, isImperial ->
                val speed = state.relativeSpeedKmh
                if (speed == null || state.isHandoffActive) "--" else {
                    if (isImperial) {
                        "${Units.kmhToMph(speed).toInt()}"
                    } else {
                        "${speed.toInt()}"
                    }
                }
            }
        )
    }
}
