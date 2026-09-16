package com.example.components

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.PlayerColorPalette
import com.example.model.Role
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
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CafeteriaComputerDialog(
    currentRole: Role,
    isPracticeMode: Boolean,
    playerColor: Color,
    playerHat: String,
    onSelectRole: (Role) -> Unit,
    onTogglePracticeMode: (Boolean) -> Unit,
    onChangeColor: (Color) -> Unit,
    onChangeHat: (String) -> Unit,
    onRollRole: (impostorChancePercent: Int) -> Unit,
    onResetPracticeDummies: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var impostorChance by remember { mutableFloatStateOf(20f) }

    val hats = listOf("🧢", "🌿", "🎀", "🎩", "🎧", "⭐", "👑", "🍦", "🚀", "🤠", "🐱", "🕶️")
    val suitColors = listOf(
        PlayerCyan, PlayerRed, PlayerBlue, PlayerGreen,
        PlayerPink, PlayerOrange, PlayerYellow, PlayerPurple
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SpaceCard),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, CyanAccent, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header (Laptop Top Bar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💻", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                        Column {
                            Text(
                                text = "Computador da Cafeteria",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Customização & Papéis de Jogo",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: 0 = Papéis (Livre), 1 = Modo & Chances, 2 = Customizar
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SpaceDark,
                    contentColor = CyanAccent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = CyanAccent
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("🎭 Papéis", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("🎲 Modo & Sorteio", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("🎨 Visual", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedTab) {
                    // TAB 0: ESCOLHA DE PAPEL DIRETA (SEM RESTRIÇÃO)
                    0 -> {
                        Text(
                            text = "Escolha seu papel diretamente:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Toque em qualquer papel para jogar com ele imediatamente.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        // Role items
                        Role.values().forEach { role ->
                            val isSelected = currentRole == role
                            val isImpostorRole = role == Role.IMPOSTOR || role == Role.METAMORFO
                            val roleBorderColor = when {
                                isSelected && isImpostorRole -> CoralRed
                                isSelected -> CyanAccent
                                else -> SpaceBorder
                            }

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) SpaceCardLighter else SpaceDark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(if (isSelected) 2.dp else 1.dp, roleBorderColor, RoundedCornerShape(12.dp))
                                    .clickable {
                                        onSelectRole(role)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = role.icon,
                                        fontSize = 24.sp,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = role.displayName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isImpostorRole) CoralRed else CyanAccent
                                            )
                                            if (isImpostorRole) {
                                                Text(
                                                    text = " [IMPOSTOR]",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = CoralRed
                                                )
                                            }
                                        }
                                        Text(
                                            text = role.description,
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(if (isImpostorRole) CoralRed else NeonGreen)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Ativo",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = SpaceDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TAB 1: MODO PRÁTICA & CHANCES DE SORTEIO
                    1 -> {
                        // Modo Prática vs Partida
                        Text(
                            text = "Modo de Jogo:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Practice button
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isPracticeMode) NeonGreen.copy(alpha = 0.2f) else SpaceDark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        if (isPracticeMode) 2.dp else 1.dp,
                                        if (isPracticeMode) NeonGreen else SpaceBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onTogglePracticeMode(true) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🛑", fontSize = 26.sp)
                                    Text(
                                        text = "Modo Prática",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isPracticeMode) NeonGreen else TextPrimary
                                    )
                                    Text(
                                        text = "Bots ficam parados!",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // Normal match button
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (!isPracticeMode) CyanAccent.copy(alpha = 0.2f) else SpaceDark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        if (!isPracticeMode) 2.dp else 1.dp,
                                        if (!isPracticeMode) CyanAccent else SpaceBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onTogglePracticeMode(false) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🏃", fontSize = 26.sp)
                                    Text(
                                        text = "Partida Normal",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (!isPracticeMode) CyanAccent else TextPrimary
                                    )
                                    Text(
                                        text = "Bots andam pela nave",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Impostor Chances Section
                        Text(
                            text = "Chance de vir Impostor no Sorteio:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "😇 Inocente: ${(100 - impostorChance.toInt())}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Text(
                                text = "😈 Impostor: ${impostorChance.toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoralRed
                            )
                        }

                        Slider(
                            value = impostorChance,
                            onValueChange = { impostorChance = it },
                            valueRange = 0f..100f,
                            steps = 3, // 0%, 25%, 50%, 75%, 100%
                            colors = SliderDefaults.colors(
                                thumbColor = CoralRed,
                                activeTrackColor = CoralRed,
                                inactiveTrackColor = CyanAccent
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Quick chance buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(0, 20, 50, 80, 100).forEach { chance ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (impostorChance.toInt() == chance) AmberGold else SpaceDark)
                                        .border(1.dp, SpaceBorder, RoundedCornerShape(8.dp))
                                        .clickable { impostorChance = chance.toFloat() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$chance%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (impostorChance.toInt() == chance) SpaceDark else TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sorteio button
                        Button(
                            onClick = {
                                onRollRole(impostorChance.toInt())
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🎲 Sortear Papel (${impostorChance.toInt()}% Impostor)",
                                fontWeight = FontWeight.Bold,
                                color = SpaceDark,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Reset Dummies button
                        Button(
                            onClick = {
                                onResetPracticeDummies()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SpaceDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SpaceBorder, RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = "🔄 Ressuscitar Bots & Limpar Pelúcias",
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // TAB 2: VISUAL (COR & CHAPÉU)
                    2 -> {
                        Text(
                            text = "Cor do Tripulante:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            suitColors.forEach { col ->
                                val isSelected = playerColor == col
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
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
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Done button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Concluir",
                        fontWeight = FontWeight.Bold,
                        color = SpaceDark,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
