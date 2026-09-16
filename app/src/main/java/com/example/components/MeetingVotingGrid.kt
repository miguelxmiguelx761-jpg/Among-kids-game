package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.model.Role
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SoftPurple
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceCardLighter
import com.example.ui.theme.SpaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MeetingVotingHeader(
    callerName: String,
    isBodyReport: Boolean,
    reportRoom: String?,
    secondsRemaining: Int,
    totalSeconds: Int = 60,
    hasUserVoted: Boolean,
    onSkipVote: () -> Unit
) {
    val progress = (secondsRemaining.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val timerColor by animateColorAsState(
        if (secondsRemaining <= 10) CoralRed else CyanAccent,
        label = "timerColor"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = SpaceCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isBodyReport) "🚨" else "📢",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            text = if (isBodyReport) "Pelúcia Encontrada!" else "Reunião de Emergência!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isBodyReport) CoralRed else AmberGold
                        )
                        Text(
                            text = if (isBodyReport && reportRoom != null) {
                                "Convocada por $callerName na $reportRoom"
                            } else {
                                "Convocada por $callerName"
                            },
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Timer Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SpaceDark)
                        .border(1.dp, timerColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⏱️ ${secondsRemaining}s",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = timerColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = timerColor,
                trackColor = SpaceDark
            )

            // Skip Vote Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (hasUserVoted) "✅ Seu voto foi registrado" else "👉 Toque em um jogador ou Pule:",
                    fontSize = 12.sp,
                    color = if (hasUserVoted) NeonGreen else TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                if (!hasUserVoted) {
                    OutlinedButton(
                        onClick = onSkipVote,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpaceBorder),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("⏭️ Pular Voto", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingVotingGrid(
    players: List<Player>,
    currentUser: Player,
    onVotePlayer: (Player) -> Unit,
    onJudgeVerdict: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(players, key = { it.id }) { player ->
            PlayerVotingCard(
                player = player,
                currentUser = currentUser,
                onVote = { onVotePlayer(player) },
                onJudge = { onJudgeVerdict(player) }
            )
        }
    }
}

@Composable
private fun PlayerVotingCard(
    player: Player,
    currentUser: Player,
    onVote: () -> Unit,
    onJudge: () -> Unit
) {
    val isMe = player.isUser
    val isDead = !player.isAlive
    val hasVoted = player.hasVoted
    val isJudgeUser = currentUser.role == Role.JUIZ && currentUser.isAlive

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) SpaceCardLighter else SpaceCard
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isMe) 1.5.dp else 1.dp,
                color = if (isMe) CyanAccent else SpaceBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .then(if (isDead) Modifier.alpha(0.5f) else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                AmongKidsAvatar(
                    player = player,
                    size = 46.dp
                )

                if (hasVoted) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(NeonGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Votou",
                            tint = SpaceDark,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val isUserAnImpostor = currentUser.role == Role.IMPOSTOR
            val isPlayerAnImpostor = player.role == Role.IMPOSTOR || player.role == Role.METAMORFO
            val showImpostorRed = isUserAnImpostor && isPlayerAnImpostor

            Text(
                text = when {
                    isMe && isUserAnImpostor -> "Você [IMPOSTOR]"
                    isMe -> "Você (${player.name})"
                    showImpostorRed -> "${player.name} 😈"
                    else -> player.name
                },
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (showImpostorRed) CoralRed else player.colorInfo.color,
                textAlign = TextAlign.Center
            )

            if (showImpostorRed) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CoralRed.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "IMPOSTOR",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CoralRed
                    )
                }
            }

            // Status label
            val statusText = when {
                isDead -> "🧸 Eliminado"
                player.isProtected -> "🛡️ Protegido"
                hasVoted -> "✅ Já votou"
                else -> "🤔 Pensando..."
            }
            Text(
                text = statusText,
                fontSize = 10.sp,
                color = if (isDead) CoralRed else TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Action Buttons
            if (!currentUser.hasVoted && currentUser.isAlive && !isDead && !isMe) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onVote,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(32.dp)
                    ) {
                        Text(
                            text = "Votar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpaceDark
                        )
                    }

                    // Juiz Instant Verdict Hammer
                    if (isJudgeUser) {
                        IconButton(
                            onClick = onJudge,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGold)
                        ) {
                            Icon(
                                Icons.Default.Gavel,
                                contentDescription = "Martelo do Juízo",
                                tint = SpaceDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
