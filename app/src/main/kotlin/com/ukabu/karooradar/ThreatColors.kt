package com.ukabu.karooradar

import androidx.compose.ui.graphics.Color

/**
 * Converts threat level to background color for datafields.
 */
fun ThreatLevel.toBackgroundColor(): Color = when (this) {
    ThreatLevel.CLEAR -> Color.Transparent
    ThreatLevel.APPROACHING -> Color(0xFFFBE401)
    ThreatLevel.WARNING -> Color(0xFFF57C00)
    ThreatLevel.CRITICAL -> Color(0xFFF44336)
}

/**
 * Returns the text color to use on top of a threat background or on the
 * default datafield background. Colored backgrounds always use black text;
 * the default background uses white in night mode and black in day mode.
 */
fun ThreatLevel.toTextColor(isNightMode: Boolean): Color = when (this) {
    ThreatLevel.CLEAR -> if (isNightMode) Color.White else Color.Black
    else -> Color.Black
}
