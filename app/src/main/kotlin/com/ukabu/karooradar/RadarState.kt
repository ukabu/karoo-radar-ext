package com.ukabu.karooradar

/**
 * Represents the current state of radar detection.
 */
data class RadarState(
    val vehicleCount: Int = 0,
    val closestDistanceMeters: Double? = null,
    val threatLevel: ThreatLevel = ThreatLevel.CLEAR,
    val relativeSpeedKmh: Double? = null,
    val absoluteSpeedKmh: Double? = null,
    val isConnected: Boolean = false,
    val closestSlotIndex: Int = -1,
    val isHandoffActive: Boolean = false,
)
