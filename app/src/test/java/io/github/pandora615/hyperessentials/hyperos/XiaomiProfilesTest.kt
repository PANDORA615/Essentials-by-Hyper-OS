package io.github.pandora615.hyperessentials.hyperos
import org.junit.Assert.*
import org.junit.Test
class XiaomiProfilesTest {
    @Test fun mi11UltraHasFirstPriority() {
        assertTrue(XiaomiProfiles.priority("star", "M2102K1G").contains("Priority 1"))
        assertTrue(XiaomiProfiles.priority("unknown", "Mi 11 Ultra").contains("Priority 1"))
        assertTrue(XiaomiProfiles.priority("ishtar", "13 Ultra").contains("Priority 2"))
        assertTrue(XiaomiProfiles.priority("unknown", "other").contains("Adaptive"))
    }
    @Test fun cameraFollowsPhysicalCutoutIncludingLeftMi11Ultra() {
        assertEquals(-1, XiaomiProfiles.cameraSide(80f, 1440))
        assertEquals(0, XiaomiProfiles.cameraSide(720f, 1440))
        assertEquals(1, XiaomiProfiles.cameraSide(1360f, 1440))
        assertEquals(0, XiaomiProfiles.cameraSide(0f, 0))
    }
}
