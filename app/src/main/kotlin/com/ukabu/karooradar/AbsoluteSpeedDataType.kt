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
 * Displays absolute speed of the closest vehicle (cyclist + relative).
 */
class AbsoluteSpeedDataType(
    private val radarExtension: RadarExtension,
) : DataTypeImpl("karoo-radar", "absolute-speed") {

    override fun startStream(emitter: Emitter<StreamState>) {
        DatafieldUtils.startDummyStream(emitter, dataTypeId)
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        DatafieldUtils.startRadarView(
            context = context,
            config = config,
            emitter = emitter,
            radarExtension = radarExtension,
            labelRes = R.string.absolute_speed,
            valueProvider = { state, isImperial ->
                val speed = state.absoluteSpeedKmh
                if (speed == null) "--" else {
                    if (isImperial) {
                        "${Units.kmhToMph(speed).toInt()}mph"
                    } else {
                        "${speed.toInt()}km/h"
                    }
                }
            }
        )
    }
}
