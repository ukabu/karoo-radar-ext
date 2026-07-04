package com.ukabu.karooradar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RadarProcessorUtilsTest {

    @Test
    fun `computeVehicleState returns count and closest distance for multiple vehicles`() {
        val ranges = listOf(150.0, 0.0, 80.0, 0.0, 0.0, 0.0, 0.0, 0.0)

        val state = computeVehicleState(ranges, threatValue = 2)

        assertEquals(2, state.vehicleCount)
        assertEquals(80.0, state.closestDistanceMeters)
        assertEquals(2, state.closestSlotIndex)
        assertEquals(ThreatLevel.WARNING, state.threatLevel)
    }

    @Test
    fun `computeVehicleState returns single vehicle`() {
        val ranges = listOf(0.0, 0.0, 0.0, 45.0, 0.0, 0.0, 0.0, 0.0)

        val state = computeVehicleState(ranges, threatValue = 1)

        assertEquals(1, state.vehicleCount)
        assertEquals(45.0, state.closestDistanceMeters)
        assertEquals(3, state.closestSlotIndex)
        assertEquals(ThreatLevel.APPROACHING, state.threatLevel)
    }

    @Test
    fun `computeVehicleState returns empty state when no vehicles`() {
        val ranges = List(8) { 0.0 }

        val state = computeVehicleState(ranges, threatValue = 0)

        assertEquals(0, state.vehicleCount)
        assertNull(state.closestDistanceMeters)
        assertEquals(-1, state.closestSlotIndex)
        assertEquals(ThreatLevel.CLEAR, state.threatLevel)
    }

    @Test
    fun `deriveAbsoluteSpeed returns sum when both speeds available`() {
        val result = deriveAbsoluteSpeed(relativeSpeedKmh = 20.0, cyclistSpeedKmh = 30.0)

        assertEquals(50.0, result)
    }

    @Test
    fun `deriveAbsoluteSpeed returns null when relative speed unavailable`() {
        val result = deriveAbsoluteSpeed(relativeSpeedKmh = null, cyclistSpeedKmh = 30.0)

        assertNull(result)
    }

    @Test
    fun `deriveAbsoluteSpeed returns null when cyclist speed unavailable`() {
        val result = deriveAbsoluteSpeed(relativeSpeedKmh = 20.0, cyclistSpeedKmh = null)

        assertNull(result)
    }

    @Test
    fun `deriveAbsoluteSpeed returns null when both unavailable`() {
        val result = deriveAbsoluteSpeed(relativeSpeedKmh = null, cyclistSpeedKmh = null)

        assertNull(result)
    }
}
