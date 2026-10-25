/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Domain Layer Models & Registries
 * File: AppTag.kt
 * Description: Domain model and business logic entry for AppTag.kt.
 */

package io.github.pandora615.hyperessentials.domain.model

data class AppTag(
    val id: String,
    val name: String,
    val colorHex: String,
    val iconName: String,
    val neverAutoFreeze: Boolean = false,
)
