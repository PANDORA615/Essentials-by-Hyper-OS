/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Domain Layer Models & Registries
 * File: LockscreenShortcut.kt
 * Description: Lock screen shortcut sides and the detected state of the system's own shortcuts.
 */

package io.github.pandora615.hyperessentials.domain.model

import io.github.pandora615.hyperessentials.data.repository.SettingsRepository

enum class LockscreenShortcutSide(
    val prefKey: String,
) {
    LEFT(SettingsRepository.KEY_LOCKSCREEN_SHORTCUT_LEFT_ACTIONS),
    RIGHT(SettingsRepository.KEY_LOCKSCREEN_SHORTCUT_RIGHT_ACTIONS),
}

// UNKNOWN until the lock screen has been scanned once
enum class SystemShortcutsState {
    UNKNOWN,
    NONE,
    PRESENT,
}
