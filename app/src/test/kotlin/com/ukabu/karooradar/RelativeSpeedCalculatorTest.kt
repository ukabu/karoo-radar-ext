package com.ukabu.karooradar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RelativeSpeedCalculatorTest {

    @Test
    fun `constant approach converges near expected speed`() {
        val calculator = RelativeSpeedCalculator()

        // Feed samples where distance decreases by 10m every second.
        // Expected raw speed: 10 m/s = 36 km/h.
        val speeds = mutableListOf<Double?>()
        for (i in 0..6) {
            val distance = 100.0 - i * 10.0
            val (speed, _) = calculator.calculate(distance, i * 1000L, 0)
            speeds.add(speed)
        }

        // Early samples may be null until the window has enough data.
        val finalSpeed = speeds.last()
        assertEquals(36.0, finalSpeed!!, 2.0)
    }

    @Test
    fun `handoff suppresses speed for exactly two ticks`() {
        val calculator = RelativeSpeedCalculator()

        // Establish a stable speed first.
        for (i in 0..4) {
            calculator.calculate(100.0 - i * 10.0, i * 1000L, 0)
        }

        // Handoff starts at t = 5000 with DEFAULT_HANDOFF_SAMPLES = 2.
        val (speed1, remaining1) = calculator.calculate(50.0, 5000L, 2)
        val (speed2, remaining2) = calculator.calculate(45.0, 6000L, remaining1)
        val (speed3, remaining3) = calculator.calculate(40.0, 7000L, remaining2)

        assertNull(speed1)
        assertEquals(1, remaining1)

        assertNull(speed2)
        assertEquals(0, remaining2)

        assertEquals(0, remaining3)
        assertEquals(30.6, speed3!!, 2.0)
    }

    @Test
    fun `negative computed speed is clamped to zero`() {
        val calculator = RelativeSpeedCalculator()

        calculator.calculate(50.0, 0L, 0)
        val (speed, _) = calculator.calculate(60.0, 1000L, 0)

        assertEquals(0.0, speed)
    }

    @Test
    fun `speed is capped at max`() {
        val calculator = RelativeSpeedCalculator()

        calculator.calculate(100.0, 0L, 0)
        val (speed, _) = calculator.calculate(0.0, 1000L, 0)

        assertEquals(RelativeSpeedCalculator.DEFAULT_MAX_RELATIVE_SPEED_KMH, speed)
    }
}
