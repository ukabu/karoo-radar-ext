package com.ukabu.karooradar

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.ukabu.karooradar.ThreatLevel
import com.ukabu.karooradar.toBackgroundColor

/**
 * Size-agnostic Glance composable for radar datafield rendering.
 */
fun radarDatafieldGlance(
    label: String,
    value: String,
    threatLevel: ThreatLevel,
) = @androidx.compose.runtime.Composable {
    val bgColor = threatLevel.toBackgroundColor()

    val textColor = if (threatLevel == ThreatLevel.CLEAR) {
        ColorProvider(Color.White)
    } else {
        ColorProvider(Color.Black)
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(bgColor)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = textColor
                )
            )
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }
    }
}
