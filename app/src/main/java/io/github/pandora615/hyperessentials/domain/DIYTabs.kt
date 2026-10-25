/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Domain Layer Models & Registries
 * File: DIYTabs.kt
 * Description: Domain model and business logic entry for DIYTabs.kt.
 */

package io.github.pandora615.hyperessentials.domain

import androidx.annotation.StringRes
import io.github.pandora615.hyperessentials.R

enum class DIYTabs(
    @StringRes val title: Int,
    val subtitle: Any,
    val iconRes: Int,
) {
    ESSENTIALS(
        R.string.tab_essentials,
        if (io.github.pandora615.hyperessentials.BuildConfig.DEBUG) "=^._.^= ∫ Debug" else "=^..^=",
        R.drawable.ic_stat_name,
    ),
    FREEZE(R.string.tab_freeze, R.string.tab_freeze_subtitle, R.drawable.rounded_mode_cool_24),
    DIY(R.string.tab_diy, R.string.tab_diy_subtitle, R.drawable.rounded_experiment_24),
}
