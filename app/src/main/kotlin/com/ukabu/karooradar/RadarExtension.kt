package com.ukabu.karooradar

import com.ukabu.karooradar.AbsoluteSpeedDataType
import com.ukabu.karooradar.ClosestDistanceDataType
import com.ukabu.karooradar.RelativeSpeedDataType
import com.ukabu.karooradar.VehicleCountDataType
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.KarooExtension
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.FitEffect
import io.hammerhead.karooext.models.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Main Karoo Extension service for radar data display and FIT recording.
 */
class RadarExtension : KarooExtension("karoo-radar", "1.0.0") {

    lateinit var karooSystem: KarooSystemService
    lateinit var radarProcessor: RadarProcessor
    private lateinit var fitRecorder: FitRecorder
    private var serviceJob: Job? = null

    override val types by lazy {
        listOf(
            VehicleCountDataType(this),
            ClosestDistanceDataType(this),
            RelativeSpeedDataType(this),
            AbsoluteSpeedDataType(this),
        )
    }

    override fun startFit(emitter: Emitter<FitEffect>) {
        fitRecorder.start(emitter)
    }

    override fun onCreate() {
        super.onCreate()
        karooSystem = KarooSystemService(applicationContext)
        radarProcessor = RadarProcessor(karooSystem)
        fitRecorder = FitRecorder(radarProcessor, karooSystem)

        serviceJob = CoroutineScope(Dispatchers.IO).launch {
            karooSystem.connect { connected ->
                // Connection state tracked via radar stream
            }

            try {
                SharedState.setImperialPreference(karooSystem.consumerFlow<UserProfile>().first())
            } catch (e: Exception) {
                // Keep default metric preference if profile is unavailable.
            }

            radarProcessor.start()
        }
    }

    override fun onDestroy() {
        radarProcessor.stop()
        fitRecorder.stop()
        karooSystem.disconnect()
        serviceJob?.cancel()
        serviceJob = null
        super.onDestroy()
    }
}
