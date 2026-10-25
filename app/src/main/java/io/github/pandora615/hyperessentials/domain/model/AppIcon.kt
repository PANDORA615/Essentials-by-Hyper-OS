/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Domain Model
 * File: AppIcon.kt
 * Description: Model enum representing customizable app launcher icons.
 */

package io.github.pandora615.hyperessentials.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pandora615.hyperessentials.R

enum class AppIcon(
    val key: String,
    val aliasName: String,
    @StringRes val titleRes: Int,
    @DrawableRes val foregroundRes: Int,
) {
    DEFAULT(
        key = "default",
        aliasName = "io.github.pandora615.hyperessentials.MainActivity",
        titleRes = R.string.app_icon_default,
        foregroundRes = R.drawable.ic_launcher_foreground,
    ),
    LEGACY(
        key = "legacy",
        aliasName = "io.github.pandora615.hyperessentials.MainActivityLegacy",
        titleRes = R.string.app_icon_legacy,
        foregroundRes = R.drawable.ic_launcher_foreground_legacy,
    ),
    BLACK(
        key = "black",
        aliasName = "io.github.pandora615.hyperessentials.MainActivityBlack",
        titleRes = R.string.app_icon_black,
        foregroundRes = R.drawable.ic_launcher_foreground_black,
    ),
    GTA(
        key = "gta",
        aliasName = "io.github.pandora615.hyperessentials.MainActivityGta",
        titleRes = R.string.app_icon_gta,
        foregroundRes = R.drawable.ic_launcher_foreground_gta,
    ),
    GOOGLE(
        key = "google",
        aliasName = "io.github.pandora615.hyperessentials.MainActivityGoogle",
        titleRes = R.string.app_icon_google,
        foregroundRes = R.drawable.ic_launcher_foreground_google,
    );

    companion object {
        fun fromKey(key: String?): AppIcon {
            return entries.find { it.key == key } ?: DEFAULT
        }
    }
}
