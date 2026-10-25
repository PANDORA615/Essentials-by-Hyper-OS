package io.github.pandora615.hyperessentials.hyperos

/** Keeps the original global setting until a verified restore, including across process restarts. */
internal class HeadsUpSettingLease(
    private val readCurrent: () -> String?,
    private val writeCurrent: (String?) -> Unit,
    private val readSaved: () -> String?,
    private val saveOriginal: (String) -> Boolean,
    private val clearOriginal: () -> Boolean,
) {
    fun apply(suppress: Boolean): Boolean {
        val saved = readSaved()
        if (suppress) {
            if (saved == null && !saveOriginal(readCurrent() ?: UNSET)) return false
            writeCurrent("0")
            return readCurrent() == "0"
        }
        if (saved == null) return true
        if (saved != UNSET && saved.toIntOrNull() == null) return false
        val target = saved.takeUnless { it == UNSET }
        writeCurrent(target)
        return readCurrent() == target && clearOriginal()
    }

    private companion object { const val UNSET = "unset" }
}
