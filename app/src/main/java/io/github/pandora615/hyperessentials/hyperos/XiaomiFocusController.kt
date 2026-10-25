package io.github.pandora615.hyperessentials.hyperos

import android.content.Context
import android.net.Uri
import android.os.Bundle
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.services.tiles.ScreenOffAccessibilityService
import io.github.pandora615.hyperessentials.utils.ShellUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Uses Xiaomi's per-app focus permission, separate from Android heads-up and notification access. */
object XiaomiFocusController {
    data class State(val status: String = "idle", val count: Int = 0)
    private val mutableState = MutableStateFlow(State())
    val state = mutableState.asStateFlow()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private const val URI = "content://statusbar.notification"
    private val validPackage = Regex("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+")
    private var lastApplied = false

    fun sync(context: Context) {
        val app = context.applicationContext
        scope.launch { mutex.withLock {
            val settings = SettingsRepository(app)
            val active = settings.isIslandEnabled() && settings.isIslandSuppressSystemHeadsUpEnabled() &&
                ScreenOffAccessibilityService.instance != null
            val prefs = app.getSharedPreferences("xiaomi_focus_originals", 0)
            if (!ShellUtils.hasPermission(app)) {
                lastApplied = false
                mutableState.value = State(if (active || prefs.all.isNotEmpty()) "permission" else "idle")
                return@withLock
            }
            if (active && lastApplied) return@withLock
            if (!active && prefs.all.isEmpty()) {
                lastApplied = false
                mutableState.value = State("idle")
                return@withLock
            }
            mutableState.value = State("working")
            if (read(app, app.packageName) == null) {
                mutableState.value = State("unsupported")
                return@withLock
            }
            val packages = if (active) app.packageManager.getInstalledApplications(0).map { it.packageName }
                else prefs.all.keys.toList()
            var checked = 0
            var failed = 0
            for (pkg in packages.filter { validPackage.matches(it) && it != app.packageName }) {
                val nowActive = settings.isIslandEnabled() && settings.isIslandSuppressSystemHeadsUpEnabled() &&
                    ScreenOffAccessibilityService.instance != null
                if (nowActive != active) { lastApplied = false; break }
                val current = read(app, pkg)
                if (current == null) { failed++; continue }
                checked++
                val target = if (active) false else prefs.getBoolean(pkg, true)
                if (active && current && !prefs.contains(pkg) && !prefs.edit().putBoolean(pkg, current).commit()) {
                    failed++; continue
                }
                if (current != target) {
                    ShellUtils.runCommandWithOutput(app,
                        "content call --uri $URI --method setShowFocus --extra package:s:$pkg --extra canShowFocus:b:$target",
                        notifyOnError = false)
                }
                if (read(app, pkg) != target) failed++
                else if (!active) prefs.edit().remove(pkg).commit()
            }
            lastApplied = active && checked > 0 && failed == 0
            mutableState.value = State(when {
                checked == 0 -> "unsupported"
                failed > 0 -> "partial"
                active -> "active"
                else -> "idle"
            }, if (active) prefs.all.size else 0)
        } }
    }

    private fun read(context: Context, pkg: String): Boolean? {
        val extras = Bundle().apply { putString("package", pkg) }
        val result = runCatching { context.contentResolver.call(Uri.parse(URI), "canShowFocus", null, extras) }.getOrNull()
        if (result?.containsKey("canShowFocus") == true) return result.getBoolean("canShowFocus")
        val output = ShellUtils.runCommandWithOutput(context,
            "content call --uri $URI --method canShowFocus --extra package:s:$pkg", notifyOnError = false).orEmpty()
        return Regex("canShowFocus=(true|false)").find(output)?.groupValues?.get(1)?.toBooleanStrictOrNull()
    }
}
