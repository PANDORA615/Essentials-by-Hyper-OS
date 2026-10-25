package io.github.pandora615.hyperessentials.hyperos

import android.content.Context
import android.provider.Settings
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.services.tiles.ScreenOffAccessibilityService
import io.github.pandora615.hyperessentials.utils.ShellUtils
import io.github.pandora615.hyperessentials.utils.StatusBarManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** One serialized owner for the global heads-up switch; never cancels app notifications. */
object IslandHeadsUpController {
    enum class Status { OFF, ACTIVE, SERVICE_NEEDED, PERMISSION_NEEDED, FAILED, RESTORE_FAILED }
    private val mutableStatus = MutableStateFlow(Status.OFF)
    val status = mutableStatus.asStateFlow()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    @Volatile private var requested = false
    private const val OWNER = "HyperIslandHeadsUp"
    private const val SETTING = "heads_up_notifications_enabled"
    private const val SNAPSHOT = "hyper_previous_heads_up"

    fun sync(context: Context) {
        val settings = SettingsRepository(context)
        request(context, settings.isIslandEnabled() && settings.isIslandSuppressSystemHeadsUpEnabled())
    }

    fun request(context: Context, suppress: Boolean) {
        val app = context.applicationContext
        XiaomiFocusController.sync(app)
        requested = suppress
        scope.launch {
            mutex.withLock {
                val wanted = requested
                val serviceReady = ScreenOffAccessibilityService.instance != null
                val active = wanted && serviceReady
                val prefs = app.getSharedPreferences(SettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)
                try {
                    if (active) StatusBarManager.requestDisable(app, OWNER, setOf(StatusBarManager.FLAG_NOTIFICATION_PEEK))
                    else StatusBarManager.requestRestore(app, OWNER)
                    val canWrite = app.checkSelfPermission("android.permission.WRITE_SECURE_SETTINGS") ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED || ShellUtils.hasPermission(app)
                    if (!canWrite && (active || prefs.contains(SNAPSHOT))) {
                        mutableStatus.value = if (active) Status.PERMISSION_NEEDED else Status.RESTORE_FAILED
                        return@withLock
                    }
                    val lease = HeadsUpSettingLease(
                        readCurrent = { Settings.Global.getString(app.contentResolver, SETTING) },
                        writeCurrent = { value ->
                            if (Settings.Global.getString(app.contentResolver, SETTING) != value) {
                                val direct = runCatching { Settings.Global.putString(app.contentResolver, SETTING, value) }.getOrDefault(false)
                                if (!direct && ShellUtils.hasPermission(app)) {
                                    val command = if (value == null) "settings delete global $SETTING"
                                        else "settings put global $SETTING ${value.toInt()}"
                                    ShellUtils.runCommandWithOutput(app, command, notifyOnError = false)
                                }
                            }
                        },
                        readSaved = { prefs.getString(SNAPSHOT, null) },
                        saveOriginal = { prefs.edit().putString(SNAPSHOT, it).commit() },
                        clearOriginal = { prefs.edit().remove(SNAPSHOT).commit() },
                    )
                    val success = lease.apply(active)
                    mutableStatus.value = when {
                        !success -> if (active) Status.FAILED else Status.RESTORE_FAILED
                        active -> Status.ACTIVE
                        wanted -> Status.SERVICE_NEEDED
                        else -> Status.OFF
                    }
                } catch (_: Exception) {
                    mutableStatus.value = if (active) Status.FAILED else Status.RESTORE_FAILED
                }
            }
        }
    }
}
