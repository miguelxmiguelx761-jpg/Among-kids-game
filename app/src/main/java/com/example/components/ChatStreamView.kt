package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.model.QuickChatCategory
import com.example.model.QuickChatMessage
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SoftPurple
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChatStreamView(
    messages: List<QuickChatMessage>,
    currentUserId: String,
    allPlayers: List<Player>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(messages, key = { it.id }) { msg ->
            val isMe = msg.senderId == currentUserId
            val senderPlayer = allPlayers.find { it.id == msg.senderId }

            ChatBubbleItem(
                message = msg,
                isCurrentUser = isMe,
                senderPlayer = senderPlayer
            )
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: QuickChatMessage,
    isCurrentUser: Boolean,
    senderPlayer: Player?
) {
    val categoryColor = Color(message.category.colorHex)

    // Special system/dev/judge styling
    val isSpecial = message.isDevCommand || message.isJudgeVerdict || message.isSystemAlert

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            if (!isCurrentUser) {
                if (senderPlayer != null) {
                    AmongKidsAvatar(
                        player = senderPlayer,
                        size = 38.dp,
                        modifier = Modifier.padding(end = 8.dp, bottom = 4.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(message.senderColor.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = message.senderHat, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Column(
                horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
            ) {
                // Sender label & category tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 3.dp, start = 4.dp, end = 4.dp)
                ) {
                    Text(
                        text = if (isCurrentUser) "Você (${message.senderName})" else message.senderName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = message.senderColor.color
                    )

                    // Category Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${message.category.icon} ${message.category.title}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = categoryColor
                        )
                    }

                    if (message.isJudgeVerdict) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AmberGold)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "⚖️ MARTELO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = SpaceDark
                            )
                        }
                    }

                    if (message.isDevCommand) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DeepIndigo)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "💻 CMD",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Bubble container
                val bubbleBg = when {
                    message.isJudgeVerdict -> Brush.horizontalGradient(
                        listOf(Color(0xFF422006), Color(0xFF78350F))
                    )
                    message.isDevCommand -> Brush.horizontalGradient(
                        listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
                    )
                    isCurrentUser -> Brush.horizontalGradient(
                        listOf(Color(0xFF1E293B), Color(0xFF2E3D56))
                    )
                    else -> Brush.horizontalGradient(
                        listOf(Color(0xFF182234), Color(0xFF1E293B))
                    )
                }

                val borderColor = when {
                    message.isJudgeVerdict -> AmberGold
                    message.isDevCommand -> CyanAccent
                    isCurrentUser -> message.senderColor.color.copy(alpha = 0.6f)
                    else -> SpaceBorder
                }

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                                bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                            )
                        )
                        .background(bubbleBg)
                        .border(
                            width = if (isSpecial) 1.5.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                                bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = message.text,
                        color = if (message.isDevCommand) CyanAccent else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = if (isSpecial) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = if (message.isDevCommand) FontFamily.Monospace else FontFamily.Default,
                        lineHeight = 18.sp
                    )
                }
            }

            if (isCurrentUser && senderPlayer != null) {
                AmongKidsAvatar(
                    player = senderPlayer,
                    size = 38.dp,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }
        }
    }
}
