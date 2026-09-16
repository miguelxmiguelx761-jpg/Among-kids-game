package com.example.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SpaceDark
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    onMove: (dx: Float, dy: Float) -> Unit,
    onStop: () -> Unit
) {
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(size)
            .pointerInput(Unit) {
                val radius = this.size.width / 2f
                val maxRadius = radius * 0.7f

                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(radius, radius)
                        val dragVector = offset - center
                        val dist = sqrt(dragVector.x * dragVector.x + dragVector.y * dragVector.y)
                        val clampedDist = min(dist, maxRadius)
                        val angle = atan2(dragVector.y, dragVector.x)

                        thumbOffset = Offset(
                            clampedDist * cos(angle),
                            clampedDist * sin(angle)
                        )
                        val normX = thumbOffset.x / maxRadius
                        val normY = thumbOffset.y / maxRadius
                        onMove(normX, normY)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newPos = thumbOffset + dragAmount
                        val dist = sqrt(newPos.x * newPos.x + newPos.y * newPos.y)
                        val clampedDist = min(dist, maxRadius)
                        val angle = atan2(newPos.y, newPos.x)

                        thumbOffset = Offset(
                            clampedDist * cos(angle),
                            clampedDist * sin(angle)
                        )
                        val normX = thumbOffset.x / maxRadius
                        val normY = thumbOffset.y / maxRadius
                        onMove(normX, normY)
                    },
                    onDragEnd = {
                        thumbOffset = Offset.Zero
                        onStop()
                    },
                    onDragCancel = {
                        thumbOffset = Offset.Zero
                        onStop()
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val outerRadius = this.size.width * 0.45f
            val thumbRadius = this.size.width * 0.18f

            // Outer ring base
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x3300E5FF), Color(0x110F172A)),
                    center = center,
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = CyanAccent.copy(alpha = 0.35f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Center deadzone ring
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = outerRadius * 0.35f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Movable Thumb Knocker
            val thumbCenter = center + thumbOffset
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyanAccent, Color(0xFF007799)),
                    center = thumbCenter,
                    radius = thumbRadius
                ),
                radius = thumbRadius,
                center = thumbCenter
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.7f),
                radius = thumbRadius,
                center = thumbCenter,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}
