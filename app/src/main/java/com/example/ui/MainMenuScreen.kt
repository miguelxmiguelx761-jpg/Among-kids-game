package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Role
import com.example.model.RoleLotteryConfig
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
import com.example.ui.theme.SpaceCardLighter
import com.example.ui.theme.SpaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainMenuScreen(
    playerColor: Color,
    playerHat: String,
    lotteryConfig: RoleLotteryConfig,
    onStartRealMatch: () -> Unit,
    onStartPracticeMode: () -> Unit,
    onUpdateLotteryConfig: (RoleLotteryConfig) -> Unit,
    onChangeColor: (Color) -> Unit,
    onChangeHat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfigDialog by remember { mutableStateOf(false) }
    var showCustomizationDialog by remember { mutableStateOf(false) }

    // Subtle floating animation for astronaut
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF04070D), Color(0xFF0B132B), Color(0xFF070B12))
                )
            )
    ) {
        // Starfield background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starPositions = listOf(
                Offset(size.width * 0.15f, size.height * 0.12f),
                Offset(size.width * 0.85f, size.height * 0.18f),
                Offset(size.width * 0.25f, size.height * 0.45f),
                Offset(size.width * 0.72f, size.height * 0.52f),
                Offset(size.width * 0.1f, size.height * 0.75f),
                Offset(size.width * 0.9f, size.height * 0.82f),
                Offset(size.width * 0.5f, size.height * 0.08f)
            )
            starPositions.forEach { pos ->
                drawCircle(color = Color.White.copy(alpha = 0.5f), radius = 2.5f, center = pos)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo & Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚀", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AMONG KIDS",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "THE SKELD • NAVE ESPACIAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        letterSpacing = 3.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("🚀", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Astronaut Avatar Preview (Floating)
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(2.dp, CyanAccent, CircleShape)
                    .clickable { showCustomizationDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.scale(1f)
                ) {
                    Text(text = playerHat, fontSize = 26.sp)
                    // Suit icon
                    Box(
                        modifier = Modifier
                            .size(36.dp, 44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(playerColor)
                            .border(1.5.dp, Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Visor
                        Box(
                            modifier = Modifier
                                .size(22.dp, 12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PlayerCyan)
                                .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Personalize Button
            Button(
                onClick = { showCustomizationDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SpaceDark),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .border(1.dp, SpaceBorder, RoundedCornerShape(10.dp))
                    .testTag("customize_visual_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Personalizar Visual", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ==================== CARD 1: DESAFIO REAL (MODO NORMAL) ====================
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, CoralRed, RoundedCornerShape(18.dp))
                    .clickable { onStartRealMatch() }
                    .testTag("start_real_match_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏃", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                            Column {
                                Text(
                                    text = "Desafio Real",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Partida Normal da Nave",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CoralRed.copy(alpha = 0.2f))
                                .border(1.dp, CoralRed, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🎲 SORTE OU AZAR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CoralRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Todos os bots se movimentam pela nave.\n• NÃO há computador para trocar de papel.\n• Seu papel é sorteado aleatoriamente ao entrar!",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Probability Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SpaceDark)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🍀 Sorte de ser Impostor:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${lotteryConfig.impostorChance}% de Chance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = CoralRed
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "⚡ Azar de ser Inocente:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${100 - lotteryConfig.impostorChance}% de Chance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = CyanAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onStartRealMatch,
                            colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_play_real_challenge")
                        ) {
                            Text(
                                text = "🚀 JOGAR DESAFIO REAL",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }

                        IconButton(
                            onClick = { showConfigDialog = true },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SpaceDark)
                                .border(1.dp, SpaceBorder, RoundedCornerShape(12.dp))
                                .testTag("btn_configure_chances")
                        ) {
                            Icon(
                                Icons.Default.Casino,
                                contentDescription = "Configurar Chances de Sorte",
                                tint = AmberGold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==================== CARD 2: MODO PRÁTICA (TREINO) ====================
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, NeonGreen, RoundedCornerShape(18.dp))
                    .clickable { onStartPracticeMode() }
                    .testTag("start_practice_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🛑", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                            Column {
                                Text(
                                    text = "Modo Prática",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Treino Livre na Nave",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGreen.copy(alpha = 0.2f))
                                .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "💻 COM COMPUTADOR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Todos os bots ficam parados nos seus postos.\n• Tem o COMPUTADOR na Cafeteria para escolher qualquer papel livremente!\n• Teste tarefas, dutos, eliminações e habilidades.",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onStartPracticeMode,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_play_practice")
                    ) {
                        Text(
                            text = "🛑 ENTRAR NO MODO PRÁTICA",
                            fontWeight = FontWeight.Black,
                            color = SpaceDark,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer info
            Text(
                text = "Desenvolvido com carinho para crianças • Sem violência",
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        // ==================== DIALOG 1: CONFIGURAR CHANCES & SORTE ====================
        if (showConfigDialog) {
            Dialog(onDismissRequest = { showConfigDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpaceCard),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, AmberGold, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎲", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                                Column {
                                    Text(
                                        text = "Chances de Sorte & Azar",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Configuração do Desafio Real",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showConfigDialog = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Chance de você tirar a SORTE de ser Impostor:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "😈 Impostor: ${lotteryConfig.impostorChance}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = CoralRed
                            )
                            Text(
                                text = "😇 Inocente: ${100 - lotteryConfig.impostorChance}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = CyanAccent
                            )
                        }

                        Slider(
                            value = lotteryConfig.impostorChance.toFloat(),
                            onValueChange = { newVal ->
                                onUpdateLotteryConfig(lotteryConfig.copy(impostorChance = newVal.toInt()))
                            },
                            valueRange = 1f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = CoralRed,
                                activeTrackColor = CoralRed,
                                inactiveTrackColor = CyanAccent
                            )
                        )

                        // Quick buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(10, 20, 35, 50, 100).forEach { pct ->
                                val isSelected = lotteryConfig.impostorChance == pct
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) AmberGold else SpaceDark)
                                        .border(1.dp, SpaceBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            onUpdateLotteryConfig(lotteryConfig.copy(impostorChance = pct))
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (pct == 10) "10% (Padrão)" else "$pct%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SpaceDark else TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Special Roles toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SpaceDark)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Incluir Papéis Especiais",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Chance de vir Xerife, Mecânico ou Metamorfo",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                            Switch(
                                checked = lotteryConfig.enableSpecialRoles,
                                onCheckedChange = { isChecked ->
                                    onUpdateLotteryConfig(lotteryConfig.copy(enableSpecialRoles = isChecked))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AmberGold,
                                    checkedTrackColor = AmberGold.copy(alpha = 0.3f)
                                )
                            )
                        }

                        if (lotteryConfig.enableSpecialRoles) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "⭐ Xerife: ${lotteryConfig.xerifeChance}%  |  🔧 Mecânico: ${lotteryConfig.mecanicoChance}%",
                                fontSize = 11.sp,
                                color = AmberGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { showConfigDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Confirmar Configuração", fontWeight = FontWeight.Bold, color = SpaceDark)
                        }
                    }
                }
            }
        }

        // ==================== DIALOG 2: CUSTOMIZAÇÃO DE VISUAL ====================
        if (showCustomizationDialog) {
            val hats = listOf("🧢", "🌿", "🎀", "🎩", "🎧", "⭐", "👑", "🍦", "🚀", "🤠", "🐱", "🕶️")
            val suitColors = listOf(
                PlayerCyan, PlayerRed, PlayerBlue, PlayerGreen,
                PlayerPink, PlayerOrange, PlayerYellow, PlayerPurple
            )

            Dialog(onDismissRequest = { showCustomizationDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpaceCard),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, CyanAccent, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎨", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                                Column {
                                    Text(
                                        text = "Personalizar Tripulante",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Escolha sua cor e chapéu",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showCustomizationDialog = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Cor do Traje:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            suitColors.forEach { col ->
                                val isSelected = playerColor == col
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(col)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Color.White else SpaceBorder,
                                            shape = CircleShape
                                        )
                                        .clickable { onChangeColor(col) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Chapéu / Acessório:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            hats.forEach { hat ->
                                val isSelected = playerHat == hat
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) SpaceCardLighter else SpaceDark)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) CyanAccent else SpaceBorder,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onChangeHat(hat) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(hat, fontSize = 22.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showCustomizationDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Salvar Visual", fontWeight = FontWeight.Bold, color = SpaceDark)
                        }
                    }
                }
            }
        }
    }
}
