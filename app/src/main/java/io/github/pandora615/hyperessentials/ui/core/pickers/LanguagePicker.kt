/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: UI Core Components
 * File: LanguagePicker.kt
 * Description: Reusable core UI component for LanguagePicker.kt.
 */

package io.github.pandora615.hyperessentials.ui.core.pickers

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.pandora615.hyperessentials.R
import io.github.pandora615.hyperessentials.ui.components.menus.SegmentedDropdownMenuItem
import io.github.pandora615.hyperessentials.ui.core.cards.ConfigPickerItem
import io.github.pandora615.hyperessentials.utils.LanguageUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LanguagePicker(
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val languages = LanguageUtils.languages
    val selectedLanguage = LanguageUtils.getLanguage(selectedLanguageCode)

    ConfigPickerItem(
        title = stringResource(R.string.label_app_language),
        selectedValue = "${selectedLanguage.nativeName} (${selectedLanguage.name})",
        iconRes = R.drawable.rounded_globe_24,
        modifier = modifier.fillMaxWidth(),
    ) {
        languages.forEach { language ->
            SegmentedDropdownMenuItem(
                text = {
                    Text(text = "${language.nativeName} (${language.name})")
                },
                onClick = {
                    onLanguageSelected(language.code)
                },
            )
        }
    }
}
