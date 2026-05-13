package com.ukabu.karooradar

/**
 * Radar threat levels from ANT+ radar profile.
 */
enum class ThreatLevel(val level: Int) {
    CLEAR(0),
    APPROACHING(1),
    WARNING(2),
    CRITICAL(3);

    companion object {
        fun fromInt(value: Int): ThreatLevel = entries.find { it.level == value } ?: CLEAR
    }
}
