/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Domain Layer Models & Registries
 * File: FreezeMode.kt
 * Description: Domain model and business logic entry for FreezeMode.kt.
 */

package io.github.pandora615.hyperessentials.domain.model

enum class FreezeMode(
    val value: Int,
) {
    FREEZE(0),
    SUSPEND(1),
    ;

    companion object {
        fun fromInt(value: Int) = entries.find { it.value == value } ?: FREEZE
    }
}
