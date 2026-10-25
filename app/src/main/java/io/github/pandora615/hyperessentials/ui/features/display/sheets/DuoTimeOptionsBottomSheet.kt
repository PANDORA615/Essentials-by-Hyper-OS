/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: UI Feature - Display
 * File: DuoTimeOptionsBottomSheet.kt
 * Description: Bottom sheet for configuring Duo time display options.
 */

package io.github.pandora615.hyperessentials.ui.features.display.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pandora615.hyperessentials.R
import io.github.pandora615.hyperessentials.ui.components.sliders.ConfigSliderItem
import io.github.pandora615.hyperessentials.ui.core.containers.RoundedCardContainer
import io.github.pandora615.hyperessentials.ui.core.sheets.EssentialsBottomSheet
import io.github.pandora615.hyperessentials.utils.HapticUtil
import io.github.pandora615.hyperessentials.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuoTimeOptionsBottomSheet(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit,
) {
    val view = LocalView.current

    EssentialsBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.duo_time_options_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
            )

            RoundedCardContainer(
                spacing = 2.dp,
                cornerRadius = 24.dp,
            ) {
                ConfigSliderItem(
                    title = stringResource(R.string.island_font_scale_title),
                    value = viewModel.duoTimeTextScale.floatValue,
                    onValueChange = {
                        HapticUtil.performUIHaptic(view)
                        viewModel.setDuoTimeTextScale(it)
                    },
                    valueRange = 0.7f..1.6f,
                    increment = 0.05f,
                    iconRes = R.drawable.rounded_format_size_24,
                    valueFormatter = { "%.2fx".format(it) },
                )
            }
        }
    }
}
