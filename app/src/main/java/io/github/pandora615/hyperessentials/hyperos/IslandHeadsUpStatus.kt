package io.github.pandora615.hyperessentials.hyperos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pandora615.hyperessentials.R

@Composable
fun IslandHeadsUpStatus() {
    val context = LocalContext.current
    val status by IslandHeadsUpController.status.collectAsState()
    LaunchedEffect(Unit) { IslandHeadsUpController.sync(context) }
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(stringResource(when (status) {
            IslandHeadsUpController.Status.OFF -> R.string.hyper_heads_up_off
            IslandHeadsUpController.Status.ACTIVE -> R.string.hyper_heads_up_active
            IslandHeadsUpController.Status.SERVICE_NEEDED -> R.string.hyper_heads_up_service
            IslandHeadsUpController.Status.PERMISSION_NEEDED -> R.string.hyper_heads_up_permission
            IslandHeadsUpController.Status.FAILED -> R.string.hyper_heads_up_failed
            IslandHeadsUpController.Status.RESTORE_FAILED -> R.string.hyper_heads_up_restore_failed
        }), style = MaterialTheme.typography.bodySmall)
        if (status != IslandHeadsUpController.Status.ACTIVE && status != IslandHeadsUpController.Status.OFF) {
            TextButton(onClick = { IslandHeadsUpController.sync(context) }) {
                Text(stringResource(R.string.hyper_heads_up_retry))
            }
        }
    }
}
