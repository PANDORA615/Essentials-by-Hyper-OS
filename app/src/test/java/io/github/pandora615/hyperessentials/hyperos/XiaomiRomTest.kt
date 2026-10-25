package io.github.pandora615.hyperessentials.hyperos

import org.junit.Assert.*
import org.junit.Test

class XiaomiRomTest {
    @Test fun detectsAllThreeBrandsIgnoringCase() {
        assertTrue(XiaomiRom.isXiaomi("Xiaomi", "xiaomi"))
        assertTrue(XiaomiRom.isXiaomi("unknown", "Redmi"))
        assertTrue(XiaomiRom.isXiaomi("unknown", "POCO"))
        assertFalse(XiaomiRom.isXiaomi("Google", "google"))
        assertFalse(XiaomiRom.isXiaomi("notxiaomi", "unknown"))
    }
    @Test fun hyperOsTakesPrecedenceOverLegacyMiuiProperty() {
        assertEquals("HyperOS OS2.0", XiaomiRom.name("OS2.0", "V14"))
        assertEquals("MIUI V14", XiaomiRom.name("", "V14"))
        assertEquals("Android", XiaomiRom.name("", ""))
    }
}
