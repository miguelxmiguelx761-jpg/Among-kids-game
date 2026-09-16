package com.example.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SpaceDark

@Composable
fun AmongKidsAvatar(
    player: Player,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    showShield: Boolean = true
) {
    val suitColor = player.colorInfo.color
    val isDead = !player.isAlive
    val isProtected = player.isProtected && showShield

    Box(
        modifier = modifier
            .size(size)
            .then(if (isDead) Modifier.alpha(0.45f) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Shield aura ring if protected
        if (isProtected) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(CyanAccent.copy(alpha = 0.25f))
            )
        }

        Canvas(modifier = Modifier.size(size * 0.85f)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Backpack (on left/back)
            val backpackWidth = w * 0.22f
            val backpackHeight = h * 0.45f
            val backpackTop = h * 0.32f
            drawRoundRect(
                color = suitColor,
                topLeft = Offset(w * 0.05f, backpackTop),
                size = Size(backpackWidth, backpackHeight),
                cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
            )
            drawRoundRect(
                color = Color(0x33000000),
                topLeft = Offset(w * 0.05f, backpackTop),
                size = Size(backpackWidth, backpackHeight),
                cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                style = Stroke(width = w * 0.04f)
            )

            // 2. Main Body Capsule
            val bodyLeft = w * 0.22f
            val bodyWidth = w * 0.65f
            val bodyHeight = h * 0.75f
            val bodyTop = h * 0.15f

            // Legs cutout using path
            val bodyPath = Path().apply {
                moveTo(bodyLeft + bodyWidth * 0.5f, bodyTop)
                // Top arc (head)
                cubicTo(
                    bodyLeft + bodyWidth * 0.95f, bodyTop,
                    bodyLeft + bodyWidth, bodyTop + bodyHeight * 0.2f,
                    bodyLeft + bodyWidth, bodyTop + bodyHeight * 0.4f
                )
                // Right side down to right leg
                lineTo(bodyLeft + bodyWidth, bodyTop + bodyHeight)
                // Right foot bottom
                lineTo(bodyLeft + bodyWidth * 0.58f, bodyTop + bodyHeight)
                // Between legs notch
                lineTo(bodyLeft + bodyWidth * 0.58f, bodyTop + bodyHeight * 0.82f)
                lineTo(bodyLeft + bodyWidth * 0.42f, bodyTop + bodyHeight * 0.82f)
                // Left leg
                lineTo(bodyLeft + bodyWidth * 0.42f, bodyTop + bodyHeight)
                lineTo(bodyLeft, bodyTop + bodyHeight)
                // Left side up
                lineTo(bodyLeft, bodyTop + bodyHeight * 0.4f)
                cubicTo(
                    bodyLeft, bodyTop + bodyHeight * 0.2f,
                    bodyLeft + bodyWidth * 0.05f, bodyTop,
                    bodyLeft + bodyWidth * 0.5f, bodyTop
                )
                close()
            }

            drawPath(path = bodyPath, color = suitColor)
            // Body Outline
            drawPath(
                path = bodyPath,
                color = Color(0x44000000),
                style = Stroke(width = w * 0.045f)
            )

            // 3. Shiny Glass Visor
            val visorLeft = w * 0.42f
            val visorTop = h * 0.26f
            val visorWidth = w * 0.48f
            val visorHeight = h * 0.28f

            // Visor outer border
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(visorLeft - w * 0.02f, visorTop - h * 0.02f),
                size = Size(visorWidth + w * 0.04f, visorHeight + h * 0.04f),
                cornerRadius = CornerRadius(visorHeight * 0.5f, visorHeight * 0.5f)
            )

            // Visor Glass (Bright Cyan Blue)
            drawRoundRect(
                color = Color(0xFF93C5FD),
                topLeft = Offset(visorLeft, visorTop),
                size = Size(visorWidth, visorHeight),
                cornerRadius = CornerRadius(visorHeight * 0.5f, visorHeight * 0.5f)
            )

            // Specular reflection glint (white pill)
            drawRoundRect(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(visorLeft + visorWidth * 0.18f, visorTop + visorHeight * 0.15f),
                size = Size(visorWidth * 0.55f, visorHeight * 0.32f),
                cornerRadius = CornerRadius(visorHeight * 0.2f, visorHeight * 0.2f)
            )

            // If dead/ghost, draw little cute angel wings or plushie band
            if (isDead) {
                // Little red X on visor
                drawLine(
                    color = Color(0xAAFFFFFF),
                    start = Offset(visorLeft + visorWidth * 0.3f, visorTop + visorHeight * 0.3f),
                    end = Offset(visorLeft + visorWidth * 0.7f, visorTop + visorHeight * 0.7f),
                    strokeWidth = w * 0.04f
                )
            }
        }

        // Cute Hat badge on top
        if (player.hatEmoji.isNotEmpty()) {
            Text(
                text = player.hatEmoji,
                fontSize = (size.value * 0.34f).sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = (size * 0.08f), y = -(size * 0.15f))
            )
        }

        // Shield sticker badge icon on corner
        if (isProtected) {
            Text(
                text = "🛡️",
                fontSize = (size.value * 0.28f).sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
            )
        }
    }
}
