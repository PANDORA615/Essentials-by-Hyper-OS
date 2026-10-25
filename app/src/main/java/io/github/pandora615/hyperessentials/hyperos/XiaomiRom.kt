package io.github.pandora615.hyperessentials.hyperos

/** Pure detection logic: branding alone does not establish MIUI/HyperOS support. */
object XiaomiRom {
    fun isXiaomi(manufacturer: String, brand: String): Boolean =
        listOf(manufacturer, brand).any {
            it.lowercase(java.util.Locale.ROOT) in setOf("xiaomi", "redmi", "poco")
        }

    fun name(hyperOs: String, miui: String): String = when {
        hyperOs.isNotBlank() -> "HyperOS $hyperOs"
        miui.isNotBlank() -> "MIUI $miui"
        else -> "Android"
    }
}
