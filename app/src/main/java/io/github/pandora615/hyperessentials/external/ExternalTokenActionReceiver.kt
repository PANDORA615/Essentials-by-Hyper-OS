/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: External
 * File: ExternalTokenActionReceiver.kt
 * Description: Receiver for automation apps that cannot hold the external control permission, authorised by a user-set token.
 */

package io.github.pandora615.hyperessentials.external

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import java.security.MessageDigest

class ExternalTokenActionReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_TOKEN = "token"
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val expected = SettingsRepository(context).getExternalControlToken()
        val provided = intent.getStringExtra(EXTRA_TOKEN)
        if (expected == null || provided == null ||
            !MessageDigest.isEqual(expected.toByteArray(), provided.toByteArray())
        ) {
            Log.w("ExternalTokenReceiver", "Rejected external control request with missing or wrong token")
            return
        }
        handleExternalControlIntent(context, intent)
    }
}
