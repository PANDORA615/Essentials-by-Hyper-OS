package io.github.pandora615.hyperessentials.ui.features.display.sheets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pandora615.hyperessentials.R
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.island.ui.components.ClockStyles
import io.github.pandora615.hyperessentials.island.ui.components.IslandClock

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HyperIslandCustomization() {
    val context = LocalContext.current
    val settings = remember { SettingsRepository(context) }
    var style by remember { mutableStateOf(ClockStyles.safe(settings.getString("hyper_clock_style", null))) }
    var size by remember { mutableStateOf(settings.getFloat("hyper_clock_size", 14f)) }
    var palette by remember { mutableStateOf(settings.getString("hyper_clock_color", "adaptive") ?: "adaptive") }
    var roundness by remember { mutableStateOf(settings.getIslandExpandedRoundness()) }
    val names = listOf(R.string.hyper_clock_rolling, R.string.hyper_clock_digital, R.string.hyper_clock_mono,
        R.string.hyper_clock_bold, R.string.hyper_clock_stacked, R.string.hyper_clock_analog)
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.hyper_clock_style), style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ClockStyles.ids.forEachIndexed { index, id ->
                FilterChip(selected = style == id, onClick = { style = id; settings.putString("hyper_clock_style", id) },
                    label = { Text(stringResource(names[index])) })
            }
        }
        Surface(color = androidx.compose.ui.graphics.Color.Black, shape = MaterialTheme.shapes.extraLarge) {
            Box(Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                IslandClock("12:34", style, androidx.compose.ui.graphics.Color.White, size)
            }
        }
        Text(stringResource(R.string.hyper_clock_size))
        Slider(value = size, onValueChange = { size = it; settings.putFloat("hyper_clock_size", it) }, valueRange = 10f..20f, steps = 9)
        Text(stringResource(R.string.hyper_clock_color))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("adaptive" to R.string.hyper_color_adaptive, "white" to R.string.hyper_color_white,
                "orange" to R.string.hyper_color_orange, "mint" to R.string.hyper_color_mint).forEach { (id, name) ->
                FilterChip(selected = palette == id, onClick = { palette = id; settings.putString("hyper_clock_color", id) },
                    label = { Text(stringResource(name)) })
            }
        }
        Text(stringResource(R.string.hyper_island_roundness))
        Slider(value = roundness.coerceIn(12f, 48f), onValueChange = { roundness = it; settings.setIslandExpandedRoundness(it) }, valueRange = 12f..48f)
    }
}
