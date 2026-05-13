package com.ukabu.karooradar

import com.ukabu.karooradar.ThreatLevel

/**
 * Converts threat level to background color resource.
 */
fun ThreatLevel.toBackgroundColor(): Int = when (this) {
    ThreatLevel.CLEAR -> 0 // neutral, no override
    ThreatLevel.APPROACHING -> com.ukabu.karooradar.R.color.radar_approaching
    ThreatLevel.WARNING -> com.ukabu.karooradar.R.color.radar_warning
    ThreatLevel.CRITICAL -> com.ukabu.karooradar.R.color.radar_critical
}
