package io.github.pandora615.hyperessentials.hyperos
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pandora615.hyperessentials.R
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.services.InputEventListenerService
import io.github.pandora615.hyperessentials.services.tiles.ScreenOffAccessibilityService
import io.github.pandora615.hyperessentials.utils.ShellUtils
import kotlinx.coroutines.delay

@Composable
fun BindingDiagnostics() {
    val context = LocalContext.current
    val settings = remember { SettingsRepository(context) }
    var revision by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { while (true) { delay(1000); revision++ } }
    DisposableEffect(Unit) {
        fun start() {
            if (settings.getBoolean("button_remap_enabled", false)) runCatching {
                context.startForegroundService(Intent(context, InputEventListenerService::class.java))
            }
        }
        val prefs = context.getSharedPreferences(SettingsRepository.PREFS_NAME, 0)
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key?.startsWith("button_remap") == true || key == "hyper_volume_preset") start()
        }
        prefs.registerOnSharedPreferenceChangeListener(listener); start()
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    val connected = remember(revision) { ScreenOffAccessibilityService.instance != null }
    val shell = remember(revision) { ShellUtils.hasPermission(context) }
    val status = remember(revision) { settings.getString("hyper_hardware_status", "waiting") }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(R.string.hyper_binding_diagnostics), style = MaterialTheme.typography.titleMedium)
            Text("Accessibility: ${if (connected) "✓" else "—"}   Shizuku / root: ${if (shell) "✓" else "—"}")
            Text(stringResource(when (status) { "receiving" -> R.string.hyper_keys_receiving; "denied" -> R.string.hyper_keys_denied; else -> R.string.hyper_keys_waiting }))
            Text(stringResource(if (settings.getString("hyper_sleep_status", "") == "failed") R.string.hyper_sleep_failed else R.string.hyper_sleep_test_help))
            TextButton(onClick = { ScreenSleep.request(context) }) { Text(stringResource(R.string.hyper_sleep_test)) }
            if (!connected) TextButton(onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }) {
                Text(stringResource(R.string.hyper_accessibility))
            }
        }
    }
}
