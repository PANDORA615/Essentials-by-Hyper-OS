package io.github.pandora615.hyperessentials.hyperos

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.github.pandora615.hyperessentials.R
import io.github.pandora615.hyperessentials.ui.theme.EssentialsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HyperOsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { EssentialsTheme { HyperOsScreen { finish() } } }
    }
}

@Composable
fun HyperOsEntryCard() {
    val context = LocalContext.current
    FilledTonalButton(
        onClick = { context.startActivity(Intent(context, HyperOsActivity::class.java)) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        contentPadding = PaddingValues(20.dp),
    ) { Text(stringResource(R.string.hyper_hub_title)) }
}

private fun openFirst(context: Context, intents: List<Intent>) {
    for (intent in intents) {
        try {
            context.startActivity(intent)
            return
        } catch (_: android.content.ActivityNotFoundException) {
        } catch (_: SecurityException) {
        }
    }
    Toast.makeText(context, R.string.hyper_unavailable, Toast.LENGTH_LONG).show()
}

private fun appDetails(context: Context) = Intent(
    Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"),
)

private fun vendorIntent(context: Context, className: String) = Intent().apply {
    component = ComponentName("com.miui.securitycenter", className)
    putExtra("extra_pkgname", context.packageName)
    putExtra("package_name", context.packageName)
}

private fun property(name: String): String = try {
    ProcessBuilder("/system/bin/getprop", name).start().inputStream.bufferedReader().use { it.readLine().orEmpty().trim() }
} catch (_: Exception) { "" }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HyperOsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    var batteryExempt by remember { mutableStateOf(false) }
    val rom by produceState("Android") {
        value = withContext(Dispatchers.IO) {
            XiaomiRom.name(property("ro.mi.os.version.name").ifBlank { property("ro.mi.os.version") }, property("ro.miui.ui.version.name"))
        }
    }
    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                batteryExempt = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
                    .isIgnoringBatteryOptimizations(context.packageName)
            }
        }
        activity.lifecycle.addObserver(observer)
        batteryExempt = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
            .isIgnoringBatteryOptimizations(context.packageName)
        onDispose { activity.lifecycle.removeObserver(observer) }
    }
    val xiaomi = XiaomiRom.isXiaomi(Build.MANUFACTURER, Build.BRAND)
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.hyper_hub_title)) }, navigationIcon = {
            TextButton(onClick = onBack) { Text(stringResource(R.string.hyper_back)) }
        })
    }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${Build.BRAND} ${Build.MODEL}", style = MaterialTheme.typography.headlineSmall)
                    Text("$rom · Android ${Build.VERSION.RELEASE}")
                    Text(stringResource(if (xiaomi) R.string.hyper_device_notice else R.string.hyper_other_device))
                }
            }
            XiaomiTools()
            Text(stringResource(R.string.hyper_setup_heading), style = MaterialTheme.typography.titleLarge)
            SetupCard(R.string.hyper_autostart, R.string.hyper_autostart_help) {
                openFirst(context, listOf(
                    vendorIntent(context, "com.miui.permcenter.autostart.AutoStartManagementActivity"),
                    vendorIntent(context, "com.miui.permcenter.permissions.PermissionsEditorActivity"),
                    appDetails(context),
                ))
            }
            SetupCard(R.string.hyper_background, if (batteryExempt) R.string.hyper_battery_exempt else R.string.hyper_background_help) {
                openFirst(context, listOf(
                    Intent().setComponent(ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"))
                        .putExtra("package_name", context.packageName).putExtra("package_label", context.getString(R.string.app_name)),
                    appDetails(context),
                    Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS),
                ))
            }
            SetupCard(R.string.hyper_permissions, R.string.hyper_permissions_help) {
                openFirst(context, listOf(vendorIntent(context, "com.miui.permcenter.permissions.PermissionsEditorActivity"), appDetails(context)))
            }
            SetupCard(R.string.hyper_notifications, R.string.hyper_notifications_help) {
                openFirst(context, listOf(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName), appDetails(context)))
            }
            SetupCard(R.string.hyper_overlay, R.string.hyper_overlay_help) {
                openFirst(context, listOf(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")), appDetails(context)))
            }
            SetupCard(R.string.hyper_accessibility, R.string.hyper_accessibility_help) {
                openFirst(context, listOf(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), appDetails(context)))
            }
            OutlinedCard(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.hyper_limits), Modifier.padding(20.dp))
            }
            TextButton(onClick = {
                openFirst(context, listOf(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/PANDORA615/Essentials-by-Hyper-OS"))))
            }) { Text(stringResource(R.string.hyper_source)) }
        }
    }
}

@Composable
private fun SetupCard(title: Int, description: Int, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(description), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.hyper_open), color = MaterialTheme.colorScheme.primary)
        }
    }
}
