package com.ukabu.karooradar

/**
 * Represents cyclist ground speed state from the Karoo GPS/SPEED stream.
 */
data class SpeedState(
    val speedMps: Double? = null,
) {
    val isAvailable: Boolean get() = speedMps != null
    val speedKmh: Double? get() = speedMps?.times(3.6)
}
