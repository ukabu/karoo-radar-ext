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
import kotlin.math.roundToInt

/**
 * Base helpers for all radar datafield types.
 */
internal object DatafieldUtils {

    /**
     * Native label size relative to the value size. Measured on device:
     * value `textSize=41sp` renders next to a native label of ~19sp, so the
     * label is ~0.46x the value. Clamped so the label stays legible in short
     * cells and never grows larger than a native header.
     */
    private const val LABEL_SIZE_RATIO = 0.43f
    private const val LABEL_SIZE_MIN_SP = 13
    private const val LABEL_SIZE_MAX_SP = 20

    private fun labelSizeSp(valueTextSizeSp: Int): Int =
        (valueTextSizeSp * LABEL_SIZE_RATIO).roundToInt()
            .coerceIn(LABEL_SIZE_MIN_SP, LABEL_SIZE_MAX_SP)

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
        emitter.onNext(UpdateGraphicConfig(showHeader = false))

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
                    label = label,
                    value = value,
                    threatLevel = state.threatLevel,
                    textSize = config.textSize,
                    labelSize = labelSizeSp(config.textSize),
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
