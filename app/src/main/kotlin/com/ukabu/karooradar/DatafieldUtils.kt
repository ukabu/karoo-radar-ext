package com.ukabu.karooradar

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import com.ukabu.karooradar.R
import com.ukabu.karooradar.RadarExtension
import com.ukabu.karooradar.radarDatafieldGlance
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.UserProfile
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Base helpers for all radar datafield types.
 */
@OptIn(ExperimentalGlanceRemoteViewsApi::class)
internal object DatafieldUtils {
    private val glance = GlanceRemoteViews()

    suspend fun getUseImperial(radarExtension: RadarExtension): Boolean {
        return try {
            val profile = radarExtension.karooSystem.consumerFlow<UserProfile>().first()
            profile.preferredUnit.distance == UserProfile.PreferredUnit.UnitType.IMPERIAL
        } catch (e: Exception) {
            false
        }
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
                val isImperial = getUseImperial(radarExtension)
                val value = valueProvider(state, isImperial)
                val label = context.getString(labelRes)

                val result = glance.compose(context, DpSize.Unspecified) {
                    radarDatafieldGlance(label, value, state.threatLevel)()
                }
                emitter.updateView(result.remoteViews)
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
