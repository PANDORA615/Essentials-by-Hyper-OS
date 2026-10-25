/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Application Activities
 * File: WallpaperStagingActivity.kt
 * Description: Activity component for reviewing and staging tomorrow's mobile wallpaper pick.
 */

package io.github.pandora615.hyperessentials.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.ui.features.wallpaper.WallpaperStagingScreen
import io.github.pandora615.hyperessentials.ui.theme.EssentialsTheme

class WallpaperStagingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val settingsRepository = SettingsRepository(this)

        setContent {
            val isPitchBlackThemeEnabled by settingsRepository.isPitchBlackThemeEnabled.collectAsState(
                initial = false,
            )

            EssentialsTheme(pitchBlackTheme = isPitchBlackThemeEnabled) {
                Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                    WallpaperStagingScreen(
                        onBack = { finish() },
                        settingsRepository = settingsRepository,
                    )
                }
            }
        }
    }
}
