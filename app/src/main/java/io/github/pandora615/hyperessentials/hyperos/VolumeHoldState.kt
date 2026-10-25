package io.github.pandora615.hyperessentials.hyperos

/** Parses getevent's labelled output without depending on device numbering or architecture. */
object VolumeHoldState {
    fun downState(line: String): Boolean? {
        if (!line.contains("EV_KEY") || !line.contains("KEY_VOLUMEDOWN")) return null
        return when (line.trim().substringAfterLast(' ').uppercase()) {
            "DOWN", "00000001" -> true
            "UP", "00000000" -> false
            else -> null // Linux value 2 is a repeat, never a release.
        }
    }
}
