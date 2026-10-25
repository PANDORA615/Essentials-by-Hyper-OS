package io.github.pandora615.hyperessentials.hyperos
import org.junit.Assert.*
import org.junit.Test
class VolumeHoldStateTest {
    @Test fun readsVolumeDownAcrossDifferentInputDevices() {
        assertEquals(true, VolumeHoldState.downState("[ 128.10] /dev/input/event7: EV_KEY KEY_VOLUMEDOWN DOWN"))
        assertEquals(false, VolumeHoldState.downState("[ 130.10] /dev/input/event2: EV_KEY KEY_VOLUMEDOWN UP"))
    }
    @Test fun autoRepeatAndOtherKeysDoNotReleaseHold() {
        assertNull(VolumeHoldState.downState("EV_KEY KEY_VOLUMEDOWN REPEAT"))
        assertNull(VolumeHoldState.downState("EV_KEY KEY_VOLUMEUP DOWN"))
        assertNull(VolumeHoldState.downState("EV_SYN SYN_REPORT 00000000"))
        assertNull(VolumeHoldState.downState("permission denied"))
    }
    @Test fun supportsNumericValues() {
        assertEquals(true, VolumeHoldState.downState("EV_KEY KEY_VOLUMEDOWN 00000001"))
        assertEquals(false, VolumeHoldState.downState("EV_KEY KEY_VOLUMEDOWN 00000000"))
        assertNull(VolumeHoldState.downState("EV_KEY KEY_VOLUMEDOWN 00000002"))
    }
}
