package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.BotEntity
import com.example.components.CafeteriaComputerDialog
import com.example.components.ClassicShipCanvas
import com.example.components.DeadPlushie
import com.example.components.RoleRevealOverlay
import com.example.components.ShipMinimapDialog
import com.example.components.TaskMinigameDialog
import com.example.components.VirtualJoystick
import com.example.model.ClassicTheSkeldMap
import com.example.model.PlayerColorPalette
import com.example.model.Role
import com.example.model.ShipTask
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PlayerBlue
import com.example.ui.theme.PlayerCyan
import com.example.ui.theme.PlayerGreen

import com.example.ui.theme.PlayerOrange
import com.example.ui.theme.PlayerPink
import com.example.ui.theme.PlayerPurple
import com.example.ui.theme.PlayerRed
import com.example.ui.theme.PlayerYellow
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun GamePlayScreen(
    currentRole: Role,
    isPracticeMode: Boolean,
    playerColor: Color,
    playerHat: String,
    onTriggerEmergencyMeeting: (callerName: String, isBodyReport: Boolean, roomName: String?) -> Unit,
    onChangeRole: (Role) -> Unit,
    onReturnToMenu: () -> Unit,
    showInitialReveal: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Player Position on The Skeld (starts in Cafeteria)
    var playerPos by remember { mutableStateOf(Offset(600f, 290f)) }
    var playerVelocity by remember { mutableStateOf(Offset.Zero) }
    var isShielded by remember { mutableStateOf(false) }

    // Customization & Game Mode States
    var currentColor by remember(playerColor) { mutableStateOf(playerColor) }
    var currentHat by remember(playerHat) { mutableStateOf(playerHat) }
    var practiceMode by remember(isPracticeMode) { mutableStateOf(isPracticeMode) }
    var showComputerDialog by remember { mutableStateOf(false) }
    var showRoleReveal by remember { mutableStateOf(showInitialReveal) }

    // Minigame & Dialog States
    var activeTask by remember { mutableStateOf<ShipTask?>(null) }
    var showMinimap by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }

    // Tasks list
    val tasks = remember { mutableStateListOf(*ClassicTheSkeldMap.initialTasks.toTypedArray()) }
    val completedTasksCount = tasks.count { it.isCompleted }
    val totalTasksCount = tasks.size

    // Vents list
    val vents = remember { ClassicTheSkeldMap.vents }

    // Initial stationary spots for Practice Mode dummies & match bots
    val initialBotList = remember {
        listOf(
            BotEntity("b1", "Azulzinho", PlayerBlue, Offset(670f, 240f), Offset(670f, 240f), "🧢"), // Cafeteria
            BotEntity("b2", "Verdinho", PlayerGreen, Offset(920f, 210f), Offset(920f, 210f), "🌿"), // Armaria
            BotEntity("b3", "Rosinha", PlayerPink, Offset(410f, 530f), Offset(410f, 530f), "🎀"), // Elétrica
            BotEntity("b4", "Laranjinha", PlayerOrange, Offset(930f, 660f), Offset(930f, 660f), "🎧"), // Escudos
            BotEntity("b5", "Amarelinho", PlayerYellow, Offset(210f, 220f), Offset(210f, 220f), "⭐"), // Motor Superior
            BotEntity("b6", "Roxinho", PlayerPurple, Offset(280f, 460f), Offset(280f, 460f), "🎩", isImpostor = true) // Segurança
        )
    }

    // Bots List
    val bots = remember {
        mutableStateListOf(*initialBotList.map { it.copy() }.toTypedArray())
    }

    // Dead plushies lying on floor
    val deadPlushies = remember {
        mutableStateListOf<DeadPlushie>(
            DeadPlushie("dead1", PlayerRed, Offset(360f, 530f), "👑") // plushie in electrical
        )
    }

    // Current room detection
    val currentRoom = remember(playerPos) { ClassicTheSkeldMap.getRoomAt(playerPos) }
    val roomName = currentRoom?.name ?: "Corredor Espacial"

    // Distance checks
    val distToEmergencyTable = remember(playerPos) {
        val dx = playerPos.x - ClassicTheSkeldMap.EMERGENCY_TABLE_POS.x
        val dy = playerPos.y - ClassicTheSkeldMap.EMERGENCY_TABLE_POS.y
        sqrt(dx * dx + dy * dy)
    }
    val isNearEmergencyTable = distToEmergencyTable < 90f

    // Distance to Customization Laptop / Computer (Cafeteria)
    val distToComputer = remember(playerPos) {
        val dx = playerPos.x - ClassicTheSkeldMap.LAPTOP_COMPUTER_POS.x
        val dy = playerPos.y - ClassicTheSkeldMap.LAPTOP_COMPUTER_POS.y
        sqrt(dx * dx + dy * dy)
    }
    val isNearComputer = practiceMode && distToComputer < 80f

    // Near task check
    val nearestTask = remember(playerPos, tasks) {
        tasks.firstOrNull { task ->
            if (task.isCompleted) return@firstOrNull false
            val dx = playerPos.x - task.position.x
            val dy = playerPos.y - task.position.y
            sqrt(dx * dx + dy * dy) < 65f
        }
    }

    // Near dead plushie check
    val nearestDeadPlushie = remember(playerPos, deadPlushies) {
        deadPlushies.firstOrNull { dead ->
            val dx = playerPos.x - dead.position.x
            val dy = playerPos.y - dead.position.y
            sqrt(dx * dx + dy * dy) < 75f
        }
    }

    // Near vent check
    val nearestVent = remember(playerPos, vents) {
        vents.firstOrNull { vent ->
            val dx = playerPos.x - vent.position.x
            val dy = playerPos.y - vent.position.y
            sqrt(dx * dx + dy * dy) < 65f
        }
    }

    // Near bot check (for Impostor kill or Protetor shield)
    val nearestBot = remember(playerPos, bots) {
        bots.firstOrNull { bot ->
            if (!bot.isAlive) return@firstOrNull false
            val dx = playerPos.x - bot.position.x
            val dy = playerPos.y - bot.position.y
            sqrt(dx * dx + dy * dy) < 70f
        }
    }

    // Game loop for player movement & bot AI
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L) // ~60 FPS

            // Update player pos
            if (playerVelocity != Offset.Zero) {
                val speed = 5.5f
                val nextPos = playerPos + playerVelocity * speed
                if (ClassicTheSkeldMap.isWalkable(nextPos)) {
                    playerPos = nextPos
                }
            }

            // Simple wandering bot AI (ONLY runs in Normal Match mode; in Practice Mode bots are PARADOS / stationary!)
            if (!practiceMode) {
                bots.forEach { bot ->
                    val dx = bot.targetPosition.x - bot.position.x
                    val dy = bot.targetPosition.y - bot.position.y
                    val dist = sqrt(dx * dx + dy * dy)
                    if (dist < 10f) {
                        // Pick new target within room
                        val randRoom = ClassicTheSkeldMap.rooms.random()
                        val rx = Random.nextFloat() * (randRoom.bounds.width - 40f) + randRoom.bounds.left + 20f
                        val ry = Random.nextFloat() * (randRoom.bounds.height - 40f) + randRoom.bounds.top + 20f
                        bot.targetPosition = Offset(rx, ry)
                    } else {
                        val step = 1.2f
                        bot.position = Offset(
                            bot.position.x + (dx / dist) * step,
                            bot.position.y + (dy / dist) * step
                        )
                    }
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Classic The Skeld Map Canvas
        ClassicShipCanvas(
            playerPosition = playerPos,
            playerColor = currentColor,
            playerHat = currentHat,
            userRole = currentRole,
            isPlayerShielded = isShielded,
            bots = bots,
            deadPlushies = deadPlushies,
            tasks = tasks,
            vents = vents,
            activeRoomName = roomName,
            isNearEmergencyButton = isNearEmergencyTable,
            isNearComputer = isNearComputer,
            showComputer = practiceMode,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top HUD Bar (Task Progress & Room Label)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Task Bar
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpaceDark.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SpaceBorder),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TAREFAS TOTAIS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Text(
                                text = "$completedTasksCount / $totalTasksCount",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        val progress = if (totalTasksCount > 0) completedTasksCount.toFloat() / totalTasksCount else 0f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = NeonGreen,
                            trackColor = Color(0xFF1E293B)
                        )
                    }
                }

                // Return to Main Menu Button
                IconButton(
                    onClick = onReturnToMenu,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SpaceDark.copy(alpha = 0.85f))
                        .border(1.dp, SpaceBorder, CircleShape)
                        .testTag("btn_return_home")
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Menu Principal", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Mode Indicator Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (practiceMode) NeonGreen.copy(alpha = 0.25f) else CoralRed.copy(alpha = 0.25f))
                        .border(1.dp, if (practiceMode) NeonGreen else CoralRed, RoundedCornerShape(10.dp))
                        .padding(horizontal = 7.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (practiceMode) "🛑 Prática" else "🎲 Desafio Real",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (practiceMode) NeonGreen else CoralRed
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Current Role Pill
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(currentRole.badgeColor.copy(alpha = 0.25f))
                            .border(1.dp, currentRole.badgeColor, RoundedCornerShape(10.dp))
                            .clickable(enabled = practiceMode) {
                                if (practiceMode) showRoleMenu = true
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${currentRole.icon} ${currentRole.displayName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentRole.badgeColor
                        )
                    }

                    if (practiceMode) {
                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false },
                            modifier = Modifier.background(SpaceCard)
                        ) {
                            Role.values().forEach { r ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${r.icon} ${r.displayName}", color = r.badgeColor, fontWeight = FontWeight.Bold)
                                    },
                                    onClick = {
                                        onChangeRole(r)
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Laptop / Computer button (ONLY in Practice Mode)
                if (practiceMode) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { showComputerDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SpaceDark.copy(alpha = 0.85f))
                            .border(1.dp, CyanAccent, CircleShape)
                    ) {
                        Text("💻", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Minimap button
                IconButton(
                    onClick = { showMinimap = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SpaceDark.copy(alpha = 0.85f))
                        .border(1.dp, CyanAccent, CircleShape)
                ) {
                    Icon(Icons.Default.Map, contentDescription = "Minimapa", tint = CyanAccent, modifier = Modifier.size(18.dp))
                }
            }

            // Current Room Banner
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpaceDark.copy(alpha = 0.8f))
                    .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "📍 $roomName",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // 3. Virtual Joystick on Bottom Left
        VirtualJoystick(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp),
            onMove = { dx, dy ->
                playerVelocity = Offset(dx, dy)
            },
            onStop = {
                playerVelocity = Offset.Zero
            }
        )

        // 4. Action Buttons on Bottom Right (USE, REPORT, KILL, VENT, EMERGENCY)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Computer Prompt (Visible when near Cafeteria Laptop)
            AnimatedVisibility(visible = isNearComputer) {
                Button(
                    onClick = { showComputerDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(44.dp).testTag("computer_button_action")
                ) {
                    Text("💻 COMPUTADOR (Papéis & Treino)", fontWeight = FontWeight.Bold, color = SpaceDark, fontSize = 12.sp)
                }
            }

            // Emergency Button Prompt (Visible when near table OR direct trigger)
            AnimatedVisibility(visible = isNearEmergencyTable) {
                Button(
                    onClick = {
                        onTriggerEmergencyMeeting("Você", false, roomName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(44.dp).testTag("emergency_button_action")
                ) {
                    Text("📢 REUNIÃO DE EMERGÊNCIA!", fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp)
                }
            }

            // Direct Meeting Button (Quick access to test meeting & chat system anytime)
            Button(
                onClick = {
                    onTriggerEmergencyMeeting("Você", false, roomName)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(38.dp).testTag("meeting_quick_button")
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = SpaceDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reunião & Chat 💬", fontWeight = FontWeight.Bold, color = SpaceDark, fontSize = 11.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // REPORT BUTTON (Active when near a dead plushie!)
                val canReport = nearestDeadPlushie != null
                Button(
                    onClick = {
                        if (canReport) {
                            onTriggerEmergencyMeeting("Você", true, roomName)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canReport) CoralRed else Color(0xFF374151)
                    ),
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🚨", fontSize = 18.sp)
                        Text(
                            "REPORT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canReport) Color.White else TextSecondary
                        )
                    }
                }

                // USE BUTTON (Active when near task or console)
                val canUse = nearestTask != null
                Button(
                    onClick = {
                        if (canUse && nearestTask != null) {
                            activeTask = nearestTask
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canUse) CyanAccent else Color(0xFF374151)
                    ),
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚙️", fontSize = 18.sp)
                        Text(
                            "USE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canUse) SpaceDark else TextSecondary
                        )
                    }
                }
            }

            // Secondary Action Row (VENT, KILL, SHIELD)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // VENT BUTTON (If Impostor, Metamorfo or Mecânico)
                val canVent = (currentRole == Role.IMPOSTOR || currentRole == Role.METAMORFO || currentRole == Role.MECANICO) && nearestVent != null
                if (currentRole == Role.IMPOSTOR || currentRole == Role.METAMORFO || currentRole == Role.MECANICO) {
                    Button(
                        onClick = {
                            if (canVent && nearestVent != null) {
                                // Jump to connected vent!
                                val targetVentId = nearestVent.connectedVentIds.firstOrNull()
                                val targetVent = vents.firstOrNull { it.id == targetVentId }
                                if (targetVent != null) {
                                    playerPos = targetVent.position
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canVent) CyanAccent else Color(0xFF2A3447)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("💨 DUTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (canVent) SpaceDark else TextSecondary)
                    }
                }

                // KILL BUTTON (With cute plushie drop! If Impostor or Metamorfo)
                val canKill = (currentRole == Role.IMPOSTOR || currentRole == Role.METAMORFO) && nearestBot != null
                if (currentRole == Role.IMPOSTOR || currentRole == Role.METAMORFO) {
                    Button(
                        onClick = {
                            if (canKill && nearestBot != null) {
                                nearestBot.isAlive = false
                                deadPlushies.add(
                                    DeadPlushie("dead_${System.currentTimeMillis()}", nearestBot.color, nearestBot.position, nearestBot.hatEmoji)
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canKill) CoralRed else Color(0xFF2A3447)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("🧸 PELÚCIA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // PROTETOR SHIELD BUTTON
                if (currentRole == Role.PROTETOR) {
                    Button(
                        onClick = {
                            isShielded = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("🛡️ ESCUDO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SpaceDark)
                    }
                }
            }
        }

        // 5. Full Minimap Dialog
        if (showMinimap) {
            ShipMinimapDialog(
                playerPosition = playerPos,
                tasks = tasks,
                onDismiss = { showMinimap = false }
            )
        }

        // 6. Task Minigame Dialog
        val taskToPlay = activeTask
        if (taskToPlay != null) {
            TaskMinigameDialog(
                task = taskToPlay,
                onCompleteTask = { completed ->
                    val idx = tasks.indexOfFirst { it.id == completed.id }
                    if (idx != -1) {
                        tasks[idx] = completed.copy(isCompleted = true)
                    }
                },
                onDismiss = { activeTask = null }
            )
        }

        // 7. Cafeteria Computer Dialog (Role Selection, Impostor Chances & Practice Setup)
        if (showComputerDialog && practiceMode) {
            CafeteriaComputerDialog(
                currentRole = currentRole,
                isPracticeMode = practiceMode,
                playerColor = currentColor,
                playerHat = currentHat,
                onSelectRole = { role ->
                    onChangeRole(role)
                    showComputerDialog = false
                    showRoleReveal = true
                },
                onTogglePracticeMode = { practice ->
                    practiceMode = practice
                },
                onChangeColor = { col ->
                    currentColor = col
                },
                onChangeHat = { hat ->
                    currentHat = hat
                },
                onRollRole = { impostorChancePercent ->
                    val isImp = Random.nextInt(100) < impostorChancePercent
                    val rolledRole = if (isImp) Role.IMPOSTOR else Role.INOCENTE
                    onChangeRole(rolledRole)
                    showRoleReveal = true
                },
                onResetPracticeDummies = {
                    deadPlushies.clear()
                    bots.clear()
                    bots.addAll(initialBotList.map { it.copy(isAlive = true) })
                },
                onDismiss = { showComputerDialog = false }
            )
        }

        // 8. Cinematic Role Reveal Overlay (SHHHHH! + Role Announcement)
        if (showRoleReveal) {
            RoleRevealOverlay(
                role = currentRole,
                onDismiss = { showRoleReveal = false }
            )
        }
    }
}
