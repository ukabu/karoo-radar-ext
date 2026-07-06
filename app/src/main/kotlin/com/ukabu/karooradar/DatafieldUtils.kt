package com.ukabu.karooradar

import android.content.Context
import com.ukabu.karooradar.R
import com.ukabu.karooradar.RadarExtension
import com.ukabu.karooradar.radarDatafieldRemoteViews
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Base helpers for all radar datafield types.
 */
internal object DatafieldUtils {

    fun getUseImperial(): Boolean {
        return SharedState.useImperial.value ?: false
    }

    fun startRadarView(
        context: Context,
        config: ViewConfig,
        emitter: ViewEmitter,
        radarExtension: RadarExtension,
        labelRes: Int,
        valueProvider: (com.ukabu.karooradar.RadarState, Boolean) -> String,
    ) {
        emitter.onNext(UpdateGraphicConfig(showHeader = true))

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

        scope.launch {
            while (isActive) {
                val state = radarExtension.radarProcessor.radarState.value
                val isImperial = getUseImperial()
                val value = valueProvider(state, isImperial)
                val label = context.getString(labelRes)

                val remoteViews = radarDatafieldRemoteViews(
                    context = context,
                    alignment = config.alignment,
                    value = value,
                    threatLevel = state.threatLevel,
                )
                emitter.updateView(remoteViews)
                delay(1000L)
            }
        }

        emitter.setCancellable { scope.cancel() }
    }

    fun startDummyStream(emitter: Emitter<StreamState>, dataTypeId: String) {
        emitter.onNext(
            StreamState.Streaming(
                DataPoint(dataTypeId = dataTypeId, values = emptyMap())
            )
        )
    }
}
