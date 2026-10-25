package io.github.pandora615.hyperessentials.hyperos

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.accessibilityservice.AccessibilityService
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.services.tiles.ScreenOffAccessibilityService
import io.github.pandora615.hyperessentials.utils.ShellUtils
import kotlinx.coroutines.*

/** Observes all Linux input devices: volume keys are not necessarily on event0. */
class HyperVolumeMonitor(private val context: Context, private val scope: CoroutineScope) {
    @Volatile private var running = false
    private var process: Process? = null
    private var reader: Job? = null
    private val main = Handler(Looper.getMainLooper())
    private var pressed = false
    private var hold: Job? = null
    private val prefs = context.getSharedPreferences(SettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)
    fun start() {
        running = true
        reader = scope.launch(Dispatchers.IO) {
            try {
                val p = ShellUtils.newProcess(context, arrayOf("getevent", "-lt")) ?: error("getevent unavailable")
                process = p
                prefs.edit().putString("hyper_hardware_status", "waiting").apply()
                p.inputStream.bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        VolumeHoldState.downState(line)?.let { down -> main.post { handle(down) } }
                    }
                }
                if (isActive && running) prefs.edit().putString("hyper_hardware_status", "denied").apply()
            } catch (_: Exception) {
                if (running) prefs.edit().putString("hyper_hardware_status", "denied").apply()
            }
        }
    }
    private fun hasScreenOffBinding(): Boolean = prefs.getBoolean("hyper_volume_preset", false) ||
        SettingsRepository(context).getRemapActions(SettingsRepository.KEY_BUTTON_REMAP_VOL_DOWN_ACTION_ON)
            .any { it is io.github.pandora615.hyperessentials.domain.diy.Action.ScreenOff }
    private fun handle(down: Boolean) {
        prefs.edit().putLong("hyper_last_volume_event", SystemClock.elapsedRealtime()).putString("hyper_hardware_status", "receiving").apply()
        if (down == pressed) return
        pressed = down
        hold?.cancel()
        val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!down || !power.isInteractive || !hasScreenOffBinding() || !prefs.getBoolean("button_remap_enabled", false)) return
        hold = scope.launch(Dispatchers.Main) {
            delay(prefs.getLong("hyper_volume_down_hold_ms", 2000L).coerceIn(500L, 5000L))
            if (!pressed || !power.isInteractive || !hasScreenOffBinding() || !prefs.getBoolean("button_remap_enabled", false)) return@launch
            if (ScreenOffAccessibilityService.instance?.isCameraForeground() == true) return@launch
            // A received accessibility event is not proof that the lock action succeeded.
            ScreenSleep.request(context)
        }
    }
    fun stop() {
        running = false
        pressed = false
        hold?.cancel()
        process?.destroy()
        process = null
        reader?.cancel()
        reader = null
        main.removeCallbacksAndMessages(null)
    }
}
