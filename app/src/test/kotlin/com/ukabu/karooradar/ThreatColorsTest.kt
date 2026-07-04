package com.ukabu.karooradar

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ThreatColorsTest {

    @Test
    fun `threat levels map to expected background colors`() {
        assertEquals(Color.Transparent, ThreatLevel.CLEAR.toBackgroundColor())
        assertEquals(Color(0xFFFBC02D), ThreatLevel.APPROACHING.toBackgroundColor())
        assertEquals(Color(0xFFF57C00), ThreatLevel.WARNING.toBackgroundColor())
        assertEquals(Color(0xFFD32F2F), ThreatLevel.CRITICAL.toBackgroundColor())
    }
}
