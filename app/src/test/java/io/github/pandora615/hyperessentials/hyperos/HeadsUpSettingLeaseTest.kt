package io.github.pandora615.hyperessentials.hyperos

import org.junit.Assert.*
import org.junit.Test

class HeadsUpSettingLeaseTest {
    private class Device(var current: String?) {
        var original: String? = null
        var writable = true
        var snapshotWritable = true
        fun controller() = HeadsUpSettingLease(
            { current }, { if (writable) current = it }, { original },
            { if (snapshotWritable) { original = it; true } else false },
            { original = null; true },
        )
    }

    @Test fun reconnectAndProcessRestartPreserveOriginalValue() {
        val device = Device("1")
        assertTrue(device.controller().apply(true))
        assertTrue(device.controller().apply(true))
        assertEquals("1", device.original)
        assertEquals("0", device.current)
        assertTrue(device.controller().apply(false))
        assertEquals("1", device.current)
        assertNull(device.original)
    }

    @Test fun absentSettingIsDeletedOnDisable() {
        val device = Device(null)
        assertTrue(device.controller().apply(true))
        assertTrue(device.controller().apply(false))
        assertNull(device.current)
        assertNull(device.original)
    }

    @Test fun failedRestoreSurvivesUntilPermissionReturns() {
        val device = Device("1")
        assertTrue(device.controller().apply(true))
        device.writable = false
        assertFalse(device.controller().apply(false))
        assertEquals("1", device.original)
        device.writable = true
        assertTrue(device.controller().apply(false))
        assertEquals("1", device.current)
    }

    @Test fun alreadyDisabledBannersRemainDisabledAfterIslandStops() {
        val device = Device("0")
        assertTrue(device.controller().apply(true))
        assertTrue(device.controller().apply(false))
        assertEquals("0", device.current)
    }

    @Test fun failedSnapshotDoesNotChangeSystemSetting() {
        val device = Device("1")
        device.snapshotWritable = false
        assertFalse(device.controller().apply(true))
        assertEquals("1", device.current)
    }

    @Test fun deniedWriteIsNotReportedAsSuccess() {
        val device = Device("1")
        device.writable = false
        assertFalse(device.controller().apply(true))
        assertEquals("1", device.current)
        assertEquals("1", device.original)
    }
}
