/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Background Services & Receivers
 * File: AppBootReceiver.kt
 * Description: Background service component for AppBootReceiver.kt.
 */

package io.github.pandora615.hyperessentials.services.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.pandora615.hyperessentials.utils.ServiceUtils

class AppBootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("AppBootReceiver", "Device rebooted, starting essential services")
            ServiceUtils.startRequiredServices(context)
        }
    }
}
