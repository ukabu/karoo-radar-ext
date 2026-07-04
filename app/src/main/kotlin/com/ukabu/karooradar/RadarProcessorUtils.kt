package com.ukabu.karooradar

/**
 * Pure, testable result of vehicle aggregation from a single radar data point.
 */
internal data class VehicleState(
    val vehicleCount: Int,
    val closestDistanceMeters: Double?,
    val closestSlotIndex: Int,
    val threatLevel: ThreatLevel,
)

/**
 * Computes vehicle count, closest vehicle distance, and threat level from the
 * eight radar target range fields and the raw threat level integer.
 */
internal fun computeVehicleState(
    ranges: List<Double>,
    threatValue: Int,
): VehicleState {
    val nonZeroRanges = ranges.mapIndexedNotNull { idx, v ->
        if (v > 0.0) idx to v else null
    }

    val vehicleCount = nonZeroRanges.size

    if (vehicleCount == 0) {
        return VehicleState(
            vehicleCount = 0,
            closestDistanceMeters = null,
            closestSlotIndex = -1,
            threatLevel = ThreatLevel.CLEAR,
        )
    }

    val (closestSlot, closestDist) = nonZeroRanges.minByOrNull { it.second }!!
    return VehicleState(
        vehicleCount = vehicleCount,
        closestDistanceMeters = closestDist,
        closestSlotIndex = closestSlot,
        threatLevel = ThreatLevel.fromInt(threatValue),
    )
}

/**
 * Computes absolute vehicle speed from relative speed and cyclist ground speed.
 * Returns null when either input is unavailable.
 */
internal fun deriveAbsoluteSpeed(
    relativeSpeedKmh: Double?,
    cyclistSpeedKmh: Double?,
): Double? {
    return if (relativeSpeedKmh != null && cyclistSpeedKmh != null) {
        relativeSpeedKmh + cyclistSpeedKmh
    } else {
        null
    }
}
