package io.github.pandora615.hyperessentials.hyperos
import org.junit.Assert.*
import org.junit.Test
class BatteryVisualPolicyTest {
    @Test fun xiaomiPluggedButTemporarilyNotChargingStillShowsConnectedState() {
        assertTrue(BatteryVisualPolicy.connected(1,4))
        assertTrue(BatteryVisualPolicy.connected(2,2))
        assertTrue(BatteryVisualPolicy.connected(0,5))
        assertFalse(BatteryVisualPolicy.connected(0,3))
    }
    @Test fun customColorsAreExactAndSaverOverridesChargingAndMedia() {
        val charge=0xff123456.toInt(); val saver=0xffabcdef.toInt()
        assertEquals(charge,BatteryVisualPolicy.accent(true,false,charge,saver))
        assertEquals(saver,BatteryVisualPolicy.accent(true,true,charge,saver))
        assertEquals(saver,BatteryVisualPolicy.accent(false,true,charge,saver))
        assertNull(BatteryVisualPolicy.accent(false,false,charge,saver))
        assertNull(BatteryVisualPolicy.accent(true,true,null,null))
    }
}
