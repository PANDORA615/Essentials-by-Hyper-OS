package io.github.pandora615.hyperessentials.hyperos
import org.junit.Assert.*
import org.junit.Test
class PreferenceSnapshotTest {
    @Test fun upgradeSnapshotPreservesTypesAndUnicodeAndLargeConfig() {
        val values=mapOf("enabled" to true,"integer" to 3,"delay" to 2000L,"size" to 14.5f,
            "color" to "#12ab34", "apps" to setOf("я", "a"),"mapping" to "ю".repeat(50000))
        assertEquals(values,PreferenceSnapshot.decode(PreferenceSnapshot.encode(values)))
    }
    @Test fun invalidOrPartialSnapshotIsNotApplied() {
        assertNull(PreferenceSnapshot.decode(byteArrayOf(1,2,3)))
        val encoded=PreferenceSnapshot.encode(mapOf("enabled" to true))
        assertNull(PreferenceSnapshot.decode(encoded.copyOf(encoded.size-1)))
    }
    @Test fun intentionalResetRemainsAnEmptySnapshot() {
        assertEquals(emptyMap<String,Any>(),PreferenceSnapshot.decode(PreferenceSnapshot.encode(emptyMap<String,Any>())))
    }
}
