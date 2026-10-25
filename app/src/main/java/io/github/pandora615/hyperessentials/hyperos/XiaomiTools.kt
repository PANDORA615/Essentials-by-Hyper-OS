package io.github.pandora615.hyperessentials.hyperos

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pandora615.hyperessentials.R
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.domain.diy.Action
import io.github.pandora615.hyperessentials.utils.ShizukuUtils
import rikka.shizuku.Shizuku

object XiaomiProfiles {
    fun priority(device: String, model: String): String = when {
        device.equals("star", true) || model.contains("11 ultra", true) -> "Mi 11 Ultra · Priority 1"
        device.lowercase() in setOf("umi", "cmi", "cas", "venus", "mars", "cupid", "zeus", "thor", "fuxi", "nuwa", "ishtar") -> "Xiaomi Mi 10–13 Ultra · Priority 2"
        else -> "Android · Adaptive"
    }
    fun cameraSide(centerX: Float, width: Int): Int = when {
        width <= 0 -> 0
        centerX < width / 3f -> -1
        centerX > width * 2f / 3f -> 1
        else -> 0
    }
}

@Composable
fun XiaomiTools() {
    val context = LocalContext.current
    val settings = remember { SettingsRepository(context) }
    var status by remember { mutableStateOf("…") }
    val refresh = {
        status = when {
            !Shizuku.pingBinder() -> context.getString(R.string.hyper_shizuku_stopped)
            Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED -> context.getString(R.string.hyper_shizuku_permission)
            else -> context.getString(R.string.hyper_shizuku_ready)
        }
    }
    DisposableEffect(Unit) {
        val binder = Shizuku.OnBinderReceivedListener { runCatching { refresh() } }
        val dead = Shizuku.OnBinderDeadListener { status = context.getString(R.string.hyper_shizuku_stopped) }
        val permission = Shizuku.OnRequestPermissionResultListener { _, result ->
            runCatching { refresh() }
            if (result == PackageManager.PERMISSION_GRANTED) settings.applyHeadsUpSuppression()
        }
        Shizuku.addRequestPermissionResultListener(permission)
        Shizuku.addBinderDeadListener(dead)
        Shizuku.addBinderReceivedListenerSticky(binder)
        runCatching { refresh() }.onFailure { status = context.getString(R.string.hyper_shizuku_stopped) }
        onDispose {
            Shizuku.removeBinderReceivedListener(binder)
            Shizuku.removeBinderDeadListener(dead)
            Shizuku.removeRequestPermissionResultListener(permission)
        }
    }
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(XiaomiProfiles.priority(Build.DEVICE, Build.MODEL), style = MaterialTheme.typography.titleMedium)
            Text("Shizuku: $status")
            Button(onClick = {
                if (ShizukuUtils.isShizukuAvailable()) ShizukuUtils.requestPermission()
                else {
                    val intent = context.packageManager.getLaunchIntentForPackage(ShizukuUtils.getShizukuPackageName(context))
                    if (intent != null) runCatching { context.startActivity(intent) }
                    else android.widget.Toast.makeText(context, R.string.hyper_shizuku_stopped, android.widget.Toast.LENGTH_LONG).show()
                }
            }) { Text(stringResource(R.string.hyper_shizuku_connect)) }
            Text(stringResource(R.string.hyper_shizuku_help))
        }
    }
    var preset by remember { mutableStateOf(settings.getBoolean("hyper_volume_preset", false)) }
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.hyper_volume_preset), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.hyper_volume_help))
            Switch(checked = preset, onCheckedChange = { enabled ->
                val prefs = context.getSharedPreferences(SettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)
                val key = SettingsRepository.KEY_BUTTON_REMAP_VOL_DOWN_ACTION_ON
                if (enabled) {
                    prefs.edit().putString("hyper_saved_volume_mapping", prefs.getString(key, null))
                        .putBoolean("hyper_saved_remap_enabled", prefs.getBoolean("button_remap_enabled", false))
                        .putBoolean("hyper_saved_pause", prefs.getBoolean("button_remap_pause_on_volume_dialog", true))
                        .putLong("hyper_saved_hold", prefs.getLong("hyper_volume_down_hold_ms", 500L)).apply()
                    settings.setRemapActions(key, listOf(Action.ScreenOff()))
                    prefs.edit().putLong("hyper_volume_down_hold_ms", 2000L).putBoolean("button_remap_enabled", true)
                        .putBoolean("button_remap_pause_on_volume_dialog", false).apply()
                } else {
                    prefs.edit().putString(key, prefs.getString("hyper_saved_volume_mapping", null))
                        .putBoolean("button_remap_enabled", prefs.getBoolean("hyper_saved_remap_enabled", false))
                        .putBoolean("button_remap_pause_on_volume_dialog", prefs.getBoolean("hyper_saved_pause", true))
                        .putLong("hyper_volume_down_hold_ms", prefs.getLong("hyper_saved_hold", 500L)).apply()
                }
                settings.putBoolean("hyper_volume_preset", enabled)
                preset = enabled
            })
        }
    }
}
