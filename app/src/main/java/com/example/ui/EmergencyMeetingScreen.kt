package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.components.ChatStreamView
import com.example.components.MeetingVotingGrid
import com.example.components.MeetingVotingHeader
import com.example.components.QuickChatModalSheet
import com.example.model.GameMap
import com.example.model.QuickChatCatalog
import com.example.model.QuickChatCategory
import com.example.model.Role
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.EmergencyMeetingViewModel
import com.example.viewmodel.MeetingTab

@Composable
fun EmergencyMeetingScreen(
    viewModel: EmergencyMeetingViewModel = viewModel(),
    onReturnToShip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val currentUser = uiState.players.firstOrNull { it.isUser }

    var isRoleMenuOpen by remember { mutableStateOf(false) }
    var isMapMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SpaceDark,
        bottomBar = {
            // Bottom Quick Bar with 1-Tap fast presets + prominent "Abrir Chat Rápido" button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SpaceDark)
                    .border(1.dp, SpaceBorder)
                    .padding(vertical = 8.dp)
            ) {
                // Horizontal quick 1-tap presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickChatCatalog.instantPresets.take(4).forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SpaceCard)
                                .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.sendQuickChatMessage(
                                        text = preset,
                                        category = QuickChatCategory.QUESTIONS
                                    )
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Main CTA Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.openQuickChat() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("open_quick_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat Rápido",
                            tint = SpaceDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "💬 ABRIR CHAT RÁPIDO",
                            fontWeight = FontWeight.Bold,
                            color = SpaceDark,
                            fontSize = 14.sp
                        )
                    }

                    // Reset meeting button
                    IconButton(
                        onClick = { viewModel.startNewMeeting(uiState.userRole, uiState.currentMap) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SpaceCard)
                            .border(1.dp, SpaceBorder, RoundedCornerShape(14.dp))
                            .testTag("restart_meeting_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Nova Reunião",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // App Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AMONG KIDS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = CyanAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AmberGold)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "ONLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpaceDark
                            )
                        }
                    }
                    Text(
                        text = "Sistema de Dedução Social e Reunião",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Header Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Return to Ship button
                    Button(
                        onClick = onReturnToShip,
                        colors = ButtonDefaults.buttonColors(containerColor = SpaceCard),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.border(1.dp, CyanAccent, RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            text = "🚀 Nave",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }

                    // Map selector button

                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SpaceCard)
                                .border(1.dp, SpaceBorder, RoundedCornerShape(10.dp))
                                .clickable { isMapMenuOpen = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${uiState.currentMap.icon} Mapa",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = isMapMenuOpen,
                            onDismissRequest = { isMapMenuOpen = false },
                            modifier = Modifier.background(SpaceCard)
                        ) {
                            GameMap.values().forEach { map ->
                                DropdownMenuItem(
                                    text = { Text("${map.icon} ${map.mapName}", color = TextPrimary) },
                                    onClick = {
                                        viewModel.switchMap(map)
                                        isMapMenuOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Role switcher pill
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(uiState.userRole.badgeColor.copy(alpha = 0.2f))
                                .border(1.dp, uiState.userRole.badgeColor, RoundedCornerShape(10.dp))
                                .clickable { isRoleMenuOpen = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("role_selector_button")
                        ) {
                            Text(
                                text = "${uiState.userRole.icon} ${uiState.userRole.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = uiState.userRole.badgeColor
                            )
                        }

                        DropdownMenu(
                            expanded = isRoleMenuOpen,
                            onDismissRequest = { isRoleMenuOpen = false },
                            modifier = Modifier.background(SpaceCard)
                        ) {
                            Role.values().forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "${role.icon} ${role.displayName}",
                                                fontWeight = FontWeight.Bold,
                                                color = role.badgeColor
                                            )
                                            Text(
                                                text = role.description,
                                                fontSize = 10.sp,
                                                color = TextSecondary,
                                                maxLines = 1
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.switchUserRole(role)
                                        isRoleMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Meeting Status & Timer Banner
            MeetingVotingHeader(
                callerName = uiState.meetingCaller?.name ?: "Tripulante",
                isBodyReport = uiState.isBodyReport,
                reportRoom = uiState.reportedRoomName,
                secondsRemaining = uiState.secondsRemaining,
                hasUserVoted = currentUser?.hasVoted == true,
                onSkipVote = { viewModel.skipVote() }
            )

            // Switcher Tabs: 💬 Chat da Reunião vs 🗳️ Votação
            TabRow(
                selectedTabIndex = uiState.activeTab.ordinal,
                containerColor = SpaceDark,
                contentColor = CyanAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.activeTab.ordinal]),
                        color = CyanAccent
                    )
                },
                divider = { Box(Modifier.fillMaxWidth().height(1.dp).background(SpaceBorder)) }
            ) {
                Tab(
                    selected = uiState.activeTab == MeetingTab.CHAT,
                    onClick = { viewModel.setTab(MeetingTab.CHAT) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "💬 Discussão (${uiState.messages.size})",
                                fontWeight = if (uiState.activeTab == MeetingTab.CHAT) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.activeTab == MeetingTab.CHAT) CyanAccent else TextSecondary
                            )
                        }
                    }
                )
                Tab(
                    selected = uiState.activeTab == MeetingTab.VOTING,
                    onClick = { viewModel.setTab(MeetingTab.VOTING) },
                    text = {
                        val votedCount = uiState.players.filter { it.isAlive && it.hasVoted }.size
                        val totalAlive = uiState.players.filter { it.isAlive }.size
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HowToVote, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "🗳️ Votos ($votedCount/$totalAlive)",
                                fontWeight = if (uiState.activeTab == MeetingTab.VOTING) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.activeTab == MeetingTab.VOTING) CyanAccent else TextSecondary
                            )
                        }
                    }
                )
            }

            // Main Content Area based on Tab
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (uiState.activeTab) {
                    MeetingTab.CHAT -> {
                        ChatStreamView(
                            messages = uiState.messages,
                            currentUserId = currentUser?.id ?: "",
                            allPlayers = uiState.players,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    MeetingTab.VOTING -> {
                        if (currentUser != null) {
                            MeetingVotingGrid(
                                players = uiState.players,
                                currentUser = currentUser,
                                onVotePlayer = { viewModel.votePlayer(it) },
                                onJudgeVerdict = { viewModel.judgeInstantVerdict(it) },
                                modifier = Modifier.fillMaxSize().padding(top = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Chat Sheet Dialog
        if (uiState.isQuickChatSheetOpen) {
            QuickChatModalSheet(
                currentUserRole = uiState.userRole,
                players = uiState.players,
                currentMap = uiState.currentMap,
                onSendMessage = { text, category, isDev, isJudge ->
                    viewModel.sendQuickChatMessage(text, category, isDev, isJudge)
                },
                onDismiss = { viewModel.closeQuickChat() }
            )
        }

        // Final Verdict Dialog (When voting concludes or Judge uses hammer)
        if (uiState.isVotingConcluded && uiState.verdictTitle != null) {
            AlertDialog(
                onDismissRequest = { /* Modal outcome */ },
                containerColor = SpaceCard,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = uiState.verdictTitle ?: "",
                        fontWeight = FontWeight.Bold,
                        color = AmberGold,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = uiState.verdictMessage ?: "",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onReturnToShip,
                            colors = ButtonDefaults.buttonColors(containerColor = SpaceDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).border(1.dp, CyanAccent, RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = "🚀 Nave",
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.startNewMeeting(uiState.userRole, uiState.currentMap)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(
                                text = "Nova Rodada 🔄",
                                fontWeight = FontWeight.Bold,
                                color = SpaceDark,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

            )
        }
    }
}
