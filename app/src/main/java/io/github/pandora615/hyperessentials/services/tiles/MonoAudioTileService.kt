/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Background Services & Receivers
 * File: MonoAudioTileService.kt
 * Description: Background service component for MonoAudioTileService.kt.
 */

package io.github.pandora615.hyperessentials.services.tiles

import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import io.github.pandora615.hyperessentials.R

class MonoAudioTileService : BaseTileService() {
    override fun getTileLabel(): String = "Mono Audio"

    override fun getTileSubtitle(): String = if (isMonoAudioEnabled()) "On" else "Off"

    override fun hasFeaturePermission(): Boolean =
        io.github.pandora615.hyperessentials.utils.PermissionUtils
            .canWriteSecureSettings(this) ||
            (
                io.github.pandora615.hyperessentials.utils.ShellUtils
                    .isAvailable(this) &&
                    io.github.pandora615.hyperessentials.utils.ShellUtils
                        .hasPermission(this)
            )

    override fun getTileIcon(): Icon = Icon.createWithResource(this, R.drawable.rounded_headphones_24)

    override fun getTileState(): Int = if (isMonoAudioEnabled()) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE

    override fun onTileClick() {
        val newState = if (isMonoAudioEnabled()) 0 else 1
        io.github.pandora615.hyperessentials.utils.ShellUtils.runCommand(
            this,
            "settings put system master_mono $newState",
            featureName = getTileLabel(),
        )
    }

    private fun isMonoAudioEnabled(): Boolean = Settings.System.getInt(contentResolver, "master_mono", 0) == 1
}
