package io.github.pandora615.hyperessentials.ui.features.display.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pandora615.hyperessentials.R
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.ui.core.cards.IconToggleItem
import io.github.pandora615.hyperessentials.ui.core.containers.RoundedCardContainer
import io.github.pandora615.hyperessentials.utils.HapticUtil

@Composable
fun IslandLauncherOnlyToggle(settingKey: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val view = LocalView.current
    val settings = remember { SettingsRepository(context) }
    var checked by remember { mutableStateOf(settings.getBoolean(settingKey, false)) }
    RoundedCardContainer(spacing = 2.dp, cornerRadius = 24.dp, modifier = modifier) {
        IconToggleItem(
            iconRes = R.drawable.rounded_home_24,
            title = stringResource(R.string.island_launcher_only_title),
            description = stringResource(R.string.island_launcher_only_desc),
            isChecked = checked,
            onCheckedChange = {
                HapticUtil.performVirtualKeyHaptic(view)
                checked = it
                settings.putBoolean(settingKey, it)
            },
        )
    }
}

@Composable
fun IslandPrefToggle(
    settingKey: String,
    iconRes: Int,
    title: String,
    modifier: Modifier = Modifier,
    defaultValue: Boolean = false,
    onChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    val settings = remember { SettingsRepository(context) }
    var checked by remember { mutableStateOf(settings.getBoolean(settingKey, defaultValue)) }
    IconToggleItem(
        iconRes = iconRes,
        title = title,
        isChecked = checked,
        onCheckedChange = {
            HapticUtil.performVirtualKeyHaptic(view)
            checked = it
            settings.putBoolean(settingKey, it)
            onChanged(it)
        },
        modifier = modifier,
    )
}

@Composable
fun rememberIslandShowsWhileLocked(): Boolean {
    val context = LocalContext.current
    return remember { SettingsRepository(context).getIslandShowWhen() != SettingsRepository.ISLAND_SHOW_WHEN_UNLOCKED }
}
