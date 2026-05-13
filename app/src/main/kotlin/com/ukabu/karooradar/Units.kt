package com.ukabu.karooradar

/**
 * Unit conversion utilities. Display uses these; FIT stores SI units directly.
 */
object Units {
    const val METERS_TO_FEET = 3.28084
    const val KMH_TO_MPH = 0.621371

    fun metersToFeet(meters: Double): Double = meters * METERS_TO_FEET
    fun kmhToMph(kmh: Double): Double = kmh * KMH_TO_MPH
}
