/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: UI Core Components
 * File: ShakeForHelp.kt
 * Description: Drop-in host that opens the contextual help sheet when the device is shaken.
 */

package io.github.pandora615.hyperessentials.ui.core.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.pandora615.hyperessentials.domain.model.Feature
import io.github.pandora615.hyperessentials.utils.HapticUtil
import io.github.pandora615.hyperessentials.utils.hardware.ShakeDetector
import io.github.pandora615.hyperessentials.viewmodels.MainViewModel

@Composable
fun ShakeForHelp(
    feature: Feature? = null,
    viewModel: MainViewModel = viewModel(),
) {
    val context = LocalContext.current
    val view = LocalView.current
    val enabled by viewModel.isShakeForHelpEnabled
    var showHelp by remember { mutableStateOf(false) }

    val currentView by rememberUpdatedState(view)
    val detector =
        remember(context) {
            ShakeDetector(
                context = context,
                onJerk = { count ->
                    if (count == 2 && !HelpSheetTracker.isAnyOpen) {
                        HapticUtil.performMicroHaptic(currentView)
                    }
                },
            ) {
                if (!HelpSheetTracker.isAnyOpen) {
                    HapticUtil.performStrongDoubleHaptic(context)
                    showHelp = true
                }
            }
        }

    LifecycleResumeEffect(enabled) {
        if (enabled) detector.start()
        onPauseOrDispose { detector.stop() }
    }

    if (showHelp) {
        val customHelp = feature?.let { FeatureCustomHelp.forFeature(it.id) }
        if (customHelp != null) {
            customHelp.sheet { showHelp = false }
        } else if (feature != null) {
            FeatureHelpBottomSheet(
                onDismissRequest = { showHelp = false },
                feature = feature,
                viewModel = viewModel,
            )
        } else {
            InstructionsBottomSheet(onDismissRequest = { showHelp = false })
        }
    }
}
