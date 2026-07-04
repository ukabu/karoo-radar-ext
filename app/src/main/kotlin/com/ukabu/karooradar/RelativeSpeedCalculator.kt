package com.ukabu.karooradar

import kotlin.math.max
import kotlin.math.min

/**
 * Derives closest-vehicle relative speed from successive distance samples.
 *
 * The calculation is a smoothed exponential moving average of `-dD/dt` over
 * approximately [speedWindowMs]. Negative values are clamped to 0 and the
 * result is capped at [maxRelativeSpeedKmh].
 *
 * During a handoff (closest target slot changed) the calculator keeps
 * collecting samples but returns null for [handoffSamples] ticks so the UI
 * can display "--" during the transition.
 */
internal class RelativeSpeedCalculator(
    private val speedWindowMs: Long = DEFAULT_SPEED_WINDOW_MS,
    private val minSpeedDtMs: Long = DEFAULT_MIN_SPEED_DT_MS,
    private val emaAlpha: Double = DEFAULT_EMA_ALPHA,
    private val maxRelativeSpeedKmh: Double = DEFAULT_MAX_RELATIVE_SPEED_KMH,
) {
    private data class DistanceSample(val time: Long, val distance: Double)

    private val distanceHistory = ArrayDeque<DistanceSample>()
    private var emaSpeed: Double = 0.0

    /**
     * Processes a new distance sample.
     *
     * @param currentDistance distance to the closest vehicle in meters
     * @param timestampMs monotonic timestamp of this sample in milliseconds
     * @param handoffRemainingSamples number of remaining handoff ticks; if > 0
     *        the function decrements it and returns null
     * @return Pair of (display speed in km/h, or null if suppressed), (remaining handoff samples)
     */
    fun calculate(
        currentDistance: Double,
        timestampMs: Long,
        handoffRemainingSamples: Int,
    ): Pair<Double?, Int> {
        distanceHistory.addLast(DistanceSample(timestampMs, currentDistance))

        if (handoffRemainingSamples > 0) {
            return null to handoffRemainingSamples - 1
        }

        // Discard samples outside the sliding window
        while (distanceHistory.isNotEmpty() && timestampMs - distanceHistory.first().time > speedWindowMs) {
            distanceHistory.removeFirst()
        }

        if (distanceHistory.size < 2) {
            return emaSpeed.takeIf { it > 0 } to 0
        }

        val oldest = distanceHistory.first()
        val newest = distanceHistory.last()
        val dtMs = newest.time - oldest.time
        if (dtMs < minSpeedDtMs) {
            return emaSpeed.takeIf { it > 0 } to 0
        }

        val dD = newest.distance - oldest.distance
        val dtSeconds = dtMs / 1000.0
        val instantaneousKmh = (-dD / dtSeconds) * 3.6 // m/s to km/h
        val clamped = max(0.0, min(instantaneousKmh, maxRelativeSpeedKmh))

        emaSpeed = if (emaSpeed == 0.0) clamped else {
            emaAlpha * clamped + (1 - emaAlpha) * emaSpeed
        }

        return emaSpeed to 0
    }

    fun reset() {
        emaSpeed = 0.0
        distanceHistory.clear()
    }

    companion object {
        internal const val DEFAULT_SPEED_WINDOW_MS = 3000L
        internal const val DEFAULT_MIN_SPEED_DT_MS = 1000L
        internal const val DEFAULT_EMA_ALPHA = 0.3
        internal const val DEFAULT_MAX_RELATIVE_SPEED_KMH = 200.0
        internal const val DEFAULT_HANDOFF_SAMPLES = 2
    }
}
