package com.ukabu.karooradar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UnitsTest {

    @Test
    fun `meters to feet conversion`() {
        val result = Units.metersToFeet(1.0)

        assertEquals(3.28084, result, 0.00001)
    }

    @Test
    fun `km per hour to mph conversion`() {
        val result = Units.kmhToMph(1.0)

        assertEquals(0.621371, result, 0.000001)
    }
}
