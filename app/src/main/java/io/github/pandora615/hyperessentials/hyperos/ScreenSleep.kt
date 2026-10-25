package io.github.pandora615.hyperessentials.hyperos
import android.content.Context
import android.os.PowerManager
import io.github.pandora615.hyperessentials.services.tiles.ScreenOffAccessibilityService
import io.github.pandora615.hyperessentials.utils.ShellUtils
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import kotlinx.coroutines.*

object ScreenSleep {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    fun request(context: Context) {
        val app = context.applicationContext
        scope.launch {
            val power = app.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!power.isInteractive) return@launch
            runCatching { ScreenOffAccessibilityService.instance?.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN) }
            delay(250)
            if (power.isInteractive && ShellUtils.hasPermission(app)) {
                withContext(Dispatchers.IO) {
                    // SLEEP, not POWER: repeating this command must never wake the phone.
                    ShellUtils.runCommandWithOutput(app, "input keyevent 223", notifyOnError = false)
                }
                delay(400)
            }
            SettingsRepository(app).putString("hyper_sleep_status", if (!power.isInteractive) "asleep" else "failed")
        }
    }
}
