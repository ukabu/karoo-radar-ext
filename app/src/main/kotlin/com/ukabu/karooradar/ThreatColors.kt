package com.ukabu.karooradar

import androidx.compose.ui.graphics.Color

/**
 * Converts threat level to background color for Glance datafields.
 */
fun ThreatLevel.toBackgroundColor(): Color = when (this) {
    ThreatLevel.CLEAR -> Color.Transparent
    ThreatLevel.APPROACHING -> Color(0xFFFBC02D)
    ThreatLevel.WARNING -> Color(0xFFF57C00)
    ThreatLevel.CRITICAL -> Color(0xFFD32F2F)
}
