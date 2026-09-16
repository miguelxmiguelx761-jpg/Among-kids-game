package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Role
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SpaceDark
import kotlinx.coroutines.delay

@Composable
fun RoleRevealOverlay(
    role: Role,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 2 phases: 1 = "SHHHHH!", 2 = Role Announcement
    var phase by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        delay(900L) // Shhh duration
        phase = 2
        delay(2200L) // Role reveal duration
        onDismiss()
    }

    val isImpostor = role == Role.IMPOSTOR

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xF504070D))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        if (phase == 1) {
            // Phase 1: "SHHHHH!"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🤫",
                    fontSize = 72.sp,
                    modifier = Modifier.scale(pulseScale)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SHHHHH!",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = CoralRed,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A rodada vai começar...",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        } else {
            // Phase 2: Role Reveal
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                // Glow avatar ring
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            if (isImpostor) CoralRed.copy(alpha = 0.2f) else CyanAccent.copy(alpha = 0.2f)
                        )
                        .border(
                            3.dp,
                            if (isImpostor) CoralRed else CyanAccent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isImpostor) "😈" else role.icon,
                        fontSize = 44.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Role Title
                Text(
                    text = if (isImpostor) "IMPOSTOR" else role.displayName.uppercase(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isImpostor) CoralRed else CyanAccent,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                Text(
                    text = if (isImpostor) {
                        "Elimine todos os tripulantes com pelúcias e sabote a nave sem ser descoberto!"
                    } else {
                        "Há 1 Impostor escondido entre nós! Encontre as pistas e conclua suas tarefas."
                    },
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Teammates hint
                if (isImpostor) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CoralRed.copy(alpha = 0.18f))
                            .border(1.dp, CoralRed, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "🔴 Seus aliados malvados aparecerão em VERMELHO na nave!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralRed,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyanAccent.copy(alpha = 0.15f))
                            .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "🕵️ O impostor está disfarçado como tripulante comum. Não confie em ninguém!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isImpostor) CoralRed else NeonGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🚀 Entrar na Nave",
                        fontWeight = FontWeight.Bold,
                        color = SpaceDark,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
