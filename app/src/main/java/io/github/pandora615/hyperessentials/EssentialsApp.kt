/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Core Application Entrypoint
 * File: EssentialsApp.kt
 * Description: Main application entrypoint for EssentialsApp.kt.
 */

package io.github.pandora615.hyperessentials

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.services.receivers.SecurityReceiver
import io.github.pandora615.hyperessentials.utils.ShizukuUtils
import eu.darken.porter.bridge.PorterShizukuBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import io.sentry.android.core.SentryAndroid
import org.lsposed.hiddenapibypass.HiddenApiBypass

class EssentialsApp : Application() {
    companion object {
        lateinit var context: Context
            private set
    }

    private val securityReceiver = SecurityReceiver()

    override fun onCreate() {
        super.onCreate()
        io.github.pandora615.hyperessentials.data.repository.SettingsRepository(this).syncTorchRestriction()
        context = applicationContext
        io.github.pandora615.hyperessentials.utils.HapticUtil.initialize(this)

        try {
            resources?.configuration
        } catch (e: Exception) {
        }

        try {
            HiddenApiBypass.setHiddenApiExemptions("")
        } catch (_: Throwable) {
        }

        PorterShizukuBridge.start(CoroutineScope(SupervisorJob() + Dispatchers.Default))
        ShizukuUtils.initialize()
        io.github.pandora615.hyperessentials.utils.CarConnectionMonitor
            .initialize(this)
        io.github.pandora615.hyperessentials.utils.LogManager
            .init(this)

        // Init Automation
        io.github.pandora615.hyperessentials.domain.diy.DIYRepository
            .init(this)
        io.github.pandora615.hyperessentials.services.automation.AutomationManager
            .init(this)
        io.github.pandora615.hyperessentials.services.CalendarSyncManager
            .init(this)
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            io.github.pandora615.hyperessentials.services.DeviceInfoSyncManager
                .init(this@EssentialsApp)
        }
        io.github.pandora615.hyperessentials.utils.ServiceUtils
            .startRequiredServices(this)

        io.github.pandora615.hyperessentials.appfunctions.AppFunctionAvailabilityManager
            .updateAvailability(this)

        // Upstream telemetry is disabled in this independent distribution.

        val intentFilter =
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_USER_PRESENT)
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(securityReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(securityReceiver, intentFilter)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(securityReceiver)
    }

    private fun initSentry() {
        val repository = SettingsRepository(this)
        val mode = repository.getString(SettingsRepository.KEY_SENTRY_REPORT_MODE, "auto")

        if (mode == "off") return

        SentryAndroid.init(this) { options ->
            options.dsn =
                "https://e105699467efe3a43a16bfbad3a63b33@o4510996760887296.ingest.de.sentry.io/4510996763312208"
            options.isEnabled = true

            options.setBeforeSend { event, hint ->
                try {
                    val throwable = event.throwable
                    val threadName = event.threads?.firstOrNull()?.name ?: Thread.currentThread().name
                    val message = event.message?.formatted ?: event.exceptions?.firstOrNull()?.value
                    io.github.pandora615.hyperessentials.utils.LogManager.saveCrashReport(
                        context = this@EssentialsApp,
                        threadName = threadName,
                        throwable = throwable,
                        customMessage = message,
                    )
                } catch (e: Exception) {
                    android.util.Log.e("EssentialsApp", "Failed to save crash report locally in Sentry callback", e)
                }

                Handler(Looper.getMainLooper()).post {
                    Toast
                        .makeText(
                            this@EssentialsApp,
                            R.string.sentry_crash_toast,
                            Toast.LENGTH_LONG,
                        ).show()
                }
                event
            }
        }
    }
}
