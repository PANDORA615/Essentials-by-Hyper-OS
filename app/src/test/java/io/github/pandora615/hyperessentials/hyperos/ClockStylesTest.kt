package io.github.pandora615.hyperessentials.hyperos
import io.github.pandora615.hyperessentials.island.ui.components.ClockStyles
import org.junit.Assert.*
import org.junit.Test
class ClockStylesTest {
    @Test fun invalidOrOldStyleFallsBackSafely() {
        assertEquals("rolling", ClockStyles.safe(null))
        assertEquals("rolling", ClockStyles.safe("old-removed-style"))
        ClockStyles.ids.forEach { assertEquals(it, ClockStyles.safe(it)) }
    }
    @Test fun analogHourHandIncludesMinutesAndWrapsAtNoon() {
        assertEquals(-90f, ClockStyles.angle(12,0),.001f)
        assertEquals(0f, ClockStyles.angle(3,0),.001f)
        assertEquals(15f, ClockStyles.angle(3,30),.001f)
        assertEquals(180f, ClockStyles.angle(21,0),.001f)
    }
}
