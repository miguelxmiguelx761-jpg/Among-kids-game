package com.example.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ClassicTheSkeldMap
import com.example.model.Player
import com.example.model.Role
import com.example.model.ShipTask
import com.example.model.ShipVent
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PlayerRed
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceCardLighter
import com.example.ui.theme.SpaceDark
import kotlin.math.cos
import kotlin.math.sin

data class DeadPlushie(
    val id: String,
    val playerColor: Color,
    val position: Offset,
    val hatEmoji: String
)

data class BotEntity(
    val id: String,
    val name: String,
    val color: Color,
    var position: Offset,
    var targetPosition: Offset,
    val hatEmoji: String,
    val isImpostor: Boolean = false,
    var isAlive: Boolean = true
)

@Composable
fun ClassicShipCanvas(
    playerPosition: Offset,
    playerColor: Color,
    playerHat: String,
    userRole: Role,
    isPlayerShielded: Boolean,
    bots: List<BotEntity>,
    deadPlushies: List<DeadPlushie>,
    tasks: List<ShipTask>,
    vents: List<ShipVent>,
    activeRoomName: String,
    isNearEmergencyButton: Boolean,
    isNearComputer: Boolean,
    showComputer: Boolean = true,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B12))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Camera scale and offset to center around player
        // Map is 1200 x 900
        // We scale so that a good portion of the ship is visible
        val scale = (canvasWidth / 550f).coerceIn(0.8f, 1.6f)
        val cameraOffsetX = canvasWidth / 2f - playerPosition.x * scale
        val cameraOffsetY = canvasHeight / 2f - playerPosition.y * scale

        // Helper coordinate transformation
        fun toScreen(pos: Offset): Offset {
            return Offset(pos.x * scale + cameraOffsetX, pos.y * scale + cameraOffsetY)
        }

        fun toScreenRect(r: Rect): Rect {
            return Rect(
                left = r.left * scale + cameraOffsetX,
                top = r.top * scale + cameraOffsetY,
                right = r.right * scale + cameraOffsetX,
                bottom = r.bottom * scale + cameraOffsetY
            )
        }

        // 1. Draw Starfield background
        drawStarfield(canvasWidth, canvasHeight, playerPosition)

        // 2. Draw Corridors (floors)
        ClassicTheSkeldMap.corridors.forEach { corr ->
            val sRect = toScreenRect(corr)
            drawRoundRect(
                color = Color(0xFF131D2E),
                topLeft = Offset(sRect.left, sRect.top),
                size = Size(sRect.width, sRect.height),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF22324C),
                topLeft = Offset(sRect.left, sRect.top),
                size = Size(sRect.width, sRect.height),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // 3. Draw Rooms
        ClassicTheSkeldMap.rooms.forEach { room ->
            val sRect = toScreenRect(room.bounds)

            // Room Floor
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF182338), Color(0xFF101726)),
                    center = Offset(sRect.left + sRect.width / 2f, sRect.top + sRect.height / 2f),
                    radius = sRect.width * 0.7f
                ),
                topLeft = Offset(sRect.left, sRect.top),
                size = Size(sRect.width, sRect.height),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
            )

            // Metal Wall Border
            drawRoundRect(
                color = Color(0xFF334A6E),
                topLeft = Offset(sRect.left, sRect.top),
                size = Size(sRect.width, sRect.height),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                style = Stroke(width = 4.dp.toPx())
            )

            // Floor Grid Pattern
            val gridStep = 40f * scale
            var gx = sRect.left + gridStep
            while (gx < sRect.right) {
                drawLine(
                    color = Color(0x15FFFFFF),
                    start = Offset(gx, sRect.top),
                    end = Offset(gx, sRect.bottom),
                    strokeWidth = 1.dp.toPx()
                )
                gx += gridStep
            }

            // Room Title on Floor
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(90, 200, 220, 255)
                    textSize = (13f * scale).coerceIn(10f, 22f)
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
                drawText(
                    "${room.icon} ${room.name.uppercase()}",
                    sRect.left + sRect.width / 2f,
                    sRect.top + 28f * scale,
                    paint
                )
            }
        }

        // 4. Draw Cafeteria Central Emergency Table & Button
        val tablePos = toScreen(ClassicTheSkeldMap.EMERGENCY_TABLE_POS)
        val tableRadius = 46f * scale

        // Wooden Table Circle
        drawCircle(
            color = Color(0xFF4A3728),
            radius = tableRadius,
            center = tablePos
        )
        drawCircle(
            color = Color(0xFF2C2016),
            radius = tableRadius,
            center = tablePos,
            style = Stroke(width = 4.dp.toPx())
        )

        // Glass center rim
        drawCircle(
            color = Color(0xFF1E293B),
            radius = tableRadius * 0.55f,
            center = tablePos
        )

        // Emergency Red Button!
        val buttonRadius = tableRadius * 0.38f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF3333), Color(0xFF990000)),
                center = tablePos,
                radius = buttonRadius
            ),
            radius = buttonRadius,
            center = tablePos
        )

        // If player is near the Emergency Button, draw glowing warning beacon!
        if (isNearEmergencyButton) {
            drawCircle(
                color = CoralRed.copy(alpha = 0.45f),
                radius = tableRadius * 1.35f,
                center = tablePos,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // 4b. Draw Customization Laptop / Computer (Cafeteria classic) - Only shown in Practice Mode
        if (showComputer) {
            val compPos = toScreen(ClassicTheSkeldMap.LAPTOP_COMPUTER_POS)
            val deskW = 34f * scale
            val deskH = 22f * scale
            // Wooden/metallic desk
            drawRoundRect(
                color = Color(0xFF334155),
                topLeft = Offset(compPos.x - deskW / 2f, compPos.y - deskH / 2f),
                size = Size(deskW, deskH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Laptop base
            val lapW = 20f * scale
            val lapH = 12f * scale
            drawRoundRect(
                color = Color(0xFF64748B),
                topLeft = Offset(compPos.x - lapW / 2f, compPos.y - lapH / 2f + 2f * scale),
                size = Size(lapW, lapH),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
            // Glowing cyan screen
            drawRoundRect(
                color = CyanAccent,
                topLeft = Offset(compPos.x - lapW * 0.42f, compPos.y - lapH * 0.72f),
                size = Size(lapW * 0.84f, lapH * 0.65f),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
            // Screen glow aura
            drawRoundRect(
                color = CyanAccent.copy(alpha = 0.35f),
                topLeft = Offset(compPos.x - lapW * 0.5f, compPos.y - lapH * 0.8f),
                size = Size(lapW, lapH * 0.8f),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
            // Near computer beacon
            if (isNearComputer) {
                drawCircle(
                    color = CyanAccent.copy(alpha = 0.6f),
                    radius = 26f * scale,
                    center = compPos,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            // Label
            drawContext.canvas.nativeCanvas.apply {
                val compPaint = android.graphics.Paint().apply {
                    color = if (isNearComputer) android.graphics.Color.CYAN else android.graphics.Color.WHITE
                    textSize = (9f * scale).coerceIn(8f, 13f)
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                    setShadowLayer(3f, 0f, 0f, android.graphics.Color.BLACK)
                }
                drawText("💻 COMPUTADOR", compPos.x, compPos.y - 16f * scale, compPaint)
            }
        }

        // 5. Draw Vents (Dutos)
        vents.forEach { vent ->
            val vPos = toScreen(vent.position)
            val ventW = 28f * scale
            val ventH = 20f * scale

            // Vent grate
            drawRoundRect(
                color = Color(0xFF1A2333),
                topLeft = Offset(vPos.x - ventW / 2f, vPos.y - ventH / 2f),
                size = Size(ventW, ventH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            drawRoundRect(
                color = CyanAccent.copy(alpha = 0.6f),
                topLeft = Offset(vPos.x - ventW / 2f, vPos.y - ventH / 2f),
                size = Size(ventW, ventH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Grille slats
            for (i in 1..3) {
                val lineX = vPos.x - ventW / 2f + (ventW / 4f) * i
                drawLine(
                    color = Color.White.copy(alpha = 0.4f),
                    start = Offset(lineX, vPos.y - ventH / 2f + 2f),
                    end = Offset(lineX, vPos.y + ventH / 2f - 2f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // 6. Draw Tasks on Consoles
        tasks.forEach { task ->
            if (!task.isCompleted) {
                val tPos = toScreen(task.position)
                val iconRadius = 14f * scale

                // Glowing yellow circle
                drawCircle(
                    color = AmberGold,
                    radius = iconRadius,
                    center = tPos
                )
                drawCircle(
                    color = BrightYellow,
                    radius = iconRadius * 1.35f,
                    center = tPos,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Draw Exclamation Mark
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = (14f * scale).coerceIn(10f, 18f)
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    drawText("!", tPos.x, tPos.y + 5f * scale, paint)
                }
            }
        }

        // 7. Draw Dead Plushie Bodies (Kid-safe, soft plushie toy lying down!)
        deadPlushies.forEach { dead ->
            val dPos = toScreen(dead.position)
            val plushieSize = 24f * scale

            // Lying plushie body
            drawRoundRect(
                color = dead.playerColor.copy(alpha = 0.8f),
                topLeft = Offset(dPos.x - plushieSize / 2f, dPos.y - plushieSize * 0.3f),
                size = Size(plushieSize, plushieSize * 0.6f),
                cornerRadius = CornerRadius(plushieSize * 0.3f, plushieSize * 0.3f)
            )
            // Little cute plushie stitches
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = plushieSize * 0.18f,
                center = Offset(dPos.x - plushieSize * 0.2f, dPos.y)
            )

            // Warning report marker above plushie
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    textSize = (16f * scale).coerceIn(12f, 22f)
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText("🚨", dPos.x, dPos.y - plushieSize * 0.6f, paint)
            }
        }

        // 8. Draw Bot Crewmates
        val isUserImpostor = userRole == Role.IMPOSTOR
        bots.filter { it.isAlive }.forEach { bot ->
            val bPos = toScreen(bot.position)
            // Inocentes see everyone with white names and no impostor tag!
            // Impostors see their fellow impostors in RED with [IMPOSTOR] tag!
            val showImpostorRed = isUserImpostor && bot.isImpostor
            val displayName = if (showImpostorRed) "${bot.name} [IMPOSTOR]" else bot.name

            drawCharacter(
                pos = bPos,
                suitColor = bot.color,
                scale = scale,
                name = displayName,
                hatEmoji = bot.hatEmoji,
                isProtected = false,
                isRedName = showImpostorRed
            )
        }

        // 9. Draw Player Character (User)
        val pScreenPos = toScreen(playerPosition)
        val playerDisplayName = if (isUserImpostor) "Você [IMPOSTOR]" else "Você"
        drawCharacter(
            pos = pScreenPos,
            suitColor = playerColor,
            scale = scale,
            name = playerDisplayName,
            hatEmoji = playerHat,
            isProtected = isPlayerShielded,
            isRedName = isUserImpostor
        )
    }
}

private fun DrawScope.drawStarfield(width: Float, height: Float, playerPos: Offset) {
    // Parallax stars
    val starSeeds = listOf(
        Offset(120f, 80f), Offset(450f, 150f), Offset(800f, 70f),
        Offset(1050f, 220f), Offset(180f, 520f), Offset(700f, 620f),
        Offset(1120f, 750f), Offset(340f, 800f), Offset(90f, 700f),
        Offset(920f, 480f), Offset(600f, 380f), Offset(500f, 880f)
    )

    starSeeds.forEach { star ->
        val px = (star.x - playerPos.x * 0.1f) % width
        val py = (star.y - playerPos.y * 0.1f) % height
        val actualX = if (px < 0) px + width else px
        val actualY = if (py < 0) py + height else py

        drawCircle(
            color = Color(0x6694A3B8),
            radius = 1.5.dp.toPx(),
            center = Offset(actualX, actualY)
        )
    }
}

private fun DrawScope.drawCharacter(
    pos: Offset,
    suitColor: Color,
    scale: Float,
    name: String,
    hatEmoji: String,
    isProtected: Boolean,
    isRedName: Boolean = false
) {
    val charW = 28f * scale
    val charH = 34f * scale

    // Shield aura if protected
    if (isProtected) {
        drawCircle(
            color = CyanAccent.copy(alpha = 0.35f),
            radius = charH * 0.75f,
            center = pos
        )
        drawCircle(
            color = CyanAccent,
            radius = charH * 0.75f,
            center = pos,
            style = Stroke(width = 2.dp.toPx())
        )
    }

    // Backpack
    val bpW = charW * 0.28f
    val bpH = charH * 0.5f
    drawRoundRect(
        color = suitColor,
        topLeft = Offset(pos.x - charW * 0.62f, pos.y - bpH * 0.3f),
        size = Size(bpW, bpH),
        cornerRadius = CornerRadius(bpW * 0.4f, bpW * 0.4f)
    )

    // Body capsule
    drawRoundRect(
        color = suitColor,
        topLeft = Offset(pos.x - charW * 0.45f, pos.y - charH * 0.5f),
        size = Size(charW * 0.9f, charH * 0.85f),
        cornerRadius = CornerRadius(charW * 0.45f, charW * 0.45f)
    )

    // Body outline
    drawRoundRect(
        color = Color(0x44000000),
        topLeft = Offset(pos.x - charW * 0.45f, pos.y - charH * 0.5f),
        size = Size(charW * 0.9f, charH * 0.85f),
        cornerRadius = CornerRadius(charW * 0.45f, charW * 0.45f),
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Feet
    val footW = charW * 0.32f
    val footH = charH * 0.25f
    drawRoundRect(
        color = suitColor,
        topLeft = Offset(pos.x - charW * 0.38f, pos.y + charH * 0.25f),
        size = Size(footW, footH),
        cornerRadius = CornerRadius(footW * 0.4f, footW * 0.4f)
    )
    drawRoundRect(
        color = suitColor,
        topLeft = Offset(pos.x + charW * 0.06f, pos.y + charH * 0.25f),
        size = Size(footW, footH),
        cornerRadius = CornerRadius(footW * 0.4f, footW * 0.4f)
    )

    // Glass Visor
    val visorW = charW * 0.52f
    val visorH = charH * 0.32f
    drawRoundRect(
        color = Color(0xFF93C5FD),
        topLeft = Offset(pos.x - charW * 0.05f, pos.y - charH * 0.32f),
        size = Size(visorW, visorH),
        cornerRadius = CornerRadius(visorH * 0.5f, visorH * 0.5f)
    )
    // Specular shine
    drawRoundRect(
        color = Color.White.copy(alpha = 0.85f),
        topLeft = Offset(pos.x + charW * 0.05f, pos.y - charH * 0.28f),
        size = Size(visorW * 0.5f, visorH * 0.35f),
        cornerRadius = CornerRadius(visorH * 0.2f, visorH * 0.2f)
    )

    // Hat Emoji & Name above head
    drawContext.canvas.nativeCanvas.apply {
        // Name
        val namePaint = android.graphics.Paint().apply {
            color = if (isRedName) android.graphics.Color.parseColor("#FF4444") else android.graphics.Color.WHITE
            textSize = (11f * scale).coerceIn(9f, 15f)
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
            if (isRedName) {
                setShadowLayer(5f, 0f, 0f, android.graphics.Color.parseColor("#990000"))
            } else {
                setShadowLayer(3f, 0f, 0f, android.graphics.Color.BLACK)
            }
        }
        drawText(name, pos.x, pos.y - charH * 0.6f, namePaint)

        // Hat
        if (hatEmoji.isNotEmpty()) {
            val hatPaint = android.graphics.Paint().apply {
                textSize = (13f * scale).coerceIn(10f, 18f)
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawText(hatEmoji, pos.x, pos.y - charH * 0.82f, hatPaint)
        }
    }
}
