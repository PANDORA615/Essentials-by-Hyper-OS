package io.github.pandora615.hyperessentials.island.ui.components
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun batteryChargingPulse(charging: Boolean): Modifier {
    if (!charging) return Modifier
    val transition = rememberInfiniteTransition(label = "batteryCharging")
    val alpha by transition.animateFloat(.45f, 1f,
        animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse), label = "chargePulse")
    return Modifier.graphicsLayer { this.alpha = alpha }
}
