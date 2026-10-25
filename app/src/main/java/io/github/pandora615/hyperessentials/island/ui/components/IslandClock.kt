package io.github.pandora615.hyperessentials.island.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pandora615.hyperessentials.island.ui.IslandTextStyles
import java.util.Calendar
import kotlin.math.*

object ClockStyles {
    val ids = listOf("rolling", "digital", "mono", "bold", "stacked", "analog")
    fun safe(id: String?) = id?.takeIf { it in ids } ?: "rolling"
    fun angle(hour: Int, minute: Int): Float = ((hour % 12) * 30 + minute * .5f) - 90
}

@Composable
fun IslandClock(text: String, style: String, color: Color, size: Float) {
    val base = IslandTextStyles.compact.copy(color = color, fontSize = size.coerceIn(10f, 20f).sp)
    when (ClockStyles.safe(style)) {
        "rolling" -> RollingText(text, style = base)
        "mono" -> Text(text, style = base.copy(fontFamily = FontFamily.Monospace), maxLines = 1, softWrap = false)
        "bold" -> Text(text, style = base.copy(fontWeight = FontWeight.Black), maxLines = 1, softWrap = false)
        "stacked" -> {
            val pieces = text.split(':', limit = 2)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(pieces.first(), style = base.copy(fontSize = 10.sp, lineHeight = 11.sp), maxLines = 1)
                Text(pieces.getOrElse(1) { "" }, style = base.copy(fontSize = 10.sp, lineHeight = 11.sp), maxLines = 1)
            }
        }
        "analog" -> {
            val now = Calendar.getInstance()
            val minute = now.get(Calendar.MINUTE)
            val hour = now.get(Calendar.HOUR)
            Canvas(Modifier.size(24.dp)) {
                val r = min(this.size.width, this.size.height) / 2f - 2.dp.toPx()
                drawCircle(color, r, style = Stroke(1.dp.toPx()))
                fun hand(degrees: Float, length: Float, thickness: Float) {
                    val a = Math.toRadians(degrees.toDouble())
                    drawLine(color, center, Offset(center.x + cos(a).toFloat() * r * length,
                        center.y + sin(a).toFloat() * r * length), thickness.dp.toPx(), StrokeCap.Round)
                }
                hand(ClockStyles.angle(hour, minute), .5f, 2f)
                hand(minute * 6f - 90f, .8f, 1.5f)
                drawCircle(color, 1.4.dp.toPx())
            }
        }
        else -> Text(text, style = base, maxLines = 1, softWrap = false)
    }
}
