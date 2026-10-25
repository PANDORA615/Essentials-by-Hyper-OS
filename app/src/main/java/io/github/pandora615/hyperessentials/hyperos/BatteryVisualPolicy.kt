package io.github.pandora615.hyperessentials.hyperos
object BatteryVisualPolicy {
    fun connected(plugged: Int, status: Int): Boolean = plugged > 0 || status == 2 || status == 5
    fun accent(charging: Boolean, saver: Boolean, chargeColor: Int?, saverColor: Int?): Int? = when {
        saver && saverColor != null -> saverColor
        charging && chargeColor != null -> chargeColor
        else -> null
    }
}
