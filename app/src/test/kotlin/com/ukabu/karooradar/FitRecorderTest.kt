package com.ukabu.karooradar

import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.FitEffect
import io.hammerhead.karooext.models.RideState
import io.hammerhead.karooext.models.WriteToRecordMesg
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FitRecorderTest {

    @Test
    fun `emits WriteToRecordMesg when recording and radar connected`() = runTest(UnconfinedTestDispatcher()) {
        val radarState = MutableStateFlow(
            RadarState(
                vehicleCount = 1,
                closestDistanceMeters = 50.0,
                threatLevel = ThreatLevel.APPROACHING,
                relativeSpeedKmh = 20.0,
                absoluteSpeedKmh = 50.0,
                isConnected = true,
            )
        )
        val rideState = MutableStateFlow<RideState>(RideState.Recording)
        val emitter: Emitter<FitEffect> = mockk(relaxed = true)
        val fitRecorder = FitRecorder(radarState, rideState)

        fitRecorder.start(emitter)

        radarState.value = RadarState(
            vehicleCount = 1,
            closestDistanceMeters = 45.0,
            threatLevel = ThreatLevel.APPROACHING,
            relativeSpeedKmh = 25.0,
            absoluteSpeedKmh = 55.0,
            isConnected = true,
        )

        val slot = slot<FitEffect>()
        verify(timeout = 1000) { emitter.onNext(capture(slot)) }

        assertTrue(slot.captured is WriteToRecordMesg)
        val message = slot.captured as WriteToRecordMesg
        assertEquals(4, message.values.size)

        fitRecorder.stop()
    }

    @Test
    fun `does not emit when ride is paused`() = runTest(UnconfinedTestDispatcher()) {
        val radarState = MutableStateFlow(
            RadarState(
                vehicleCount = 1,
                closestDistanceMeters = 50.0,
                threatLevel = ThreatLevel.APPROACHING,
                relativeSpeedKmh = 20.0,
                absoluteSpeedKmh = 50.0,
                isConnected = true,
            )
        )
        val rideState = MutableStateFlow<RideState>(RideState.Paused(auto = false))
        val emitter: Emitter<FitEffect> = mockk(relaxed = true)
        val fitRecorder = FitRecorder(radarState, rideState)

        fitRecorder.start(emitter)

        radarState.value = RadarState(
            vehicleCount = 1,
            closestDistanceMeters = 40.0,
            threatLevel = ThreatLevel.WARNING,
            relativeSpeedKmh = 30.0,
            absoluteSpeedKmh = 60.0,
            isConnected = true,
        )

        verify(exactly = 0) { emitter.onNext(any()) }

        fitRecorder.stop()
    }

    @Test
    fun `does not emit when radar is disconnected`() = runTest(UnconfinedTestDispatcher()) {
        val radarState = MutableStateFlow(
            RadarState(
                vehicleCount = 0,
                closestDistanceMeters = null,
                threatLevel = ThreatLevel.CLEAR,
                relativeSpeedKmh = null,
                absoluteSpeedKmh = null,
                isConnected = false,
            )
        )
        val rideState = MutableStateFlow<RideState>(RideState.Recording)
        val emitter: Emitter<FitEffect> = mockk(relaxed = true)
        val fitRecorder = FitRecorder(radarState, rideState)

        fitRecorder.start(emitter)

        radarState.value = RadarState(
            vehicleCount = 0,
            closestDistanceMeters = null,
            threatLevel = ThreatLevel.CLEAR,
            relativeSpeedKmh = null,
            absoluteSpeedKmh = null,
            isConnected = false,
        )

        verify(exactly = 0) { emitter.onNext(any()) }

        fitRecorder.stop()
    }
}
