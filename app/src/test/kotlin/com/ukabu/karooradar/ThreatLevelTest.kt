package com.ukabu.karooradar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ThreatLevelTest {

    @ParameterizedTest
    @CsvSource(
        "0, CLEAR",
        "1, APPROACHING",
        "2, WARNING",
        "3, CRITICAL",
    )
    fun `fromInt maps valid threat values`(input: Int, expected: ThreatLevel) {
        assertEquals(expected, ThreatLevel.fromInt(input))
    }

    @ParameterizedTest
    @CsvSource(
        "-1",
        "4",
        "99",
    )
    fun `fromInt defaults to CLEAR for out of range values`(input: Int) {
        assertEquals(ThreatLevel.CLEAR, ThreatLevel.fromInt(input))
    }
}
