package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMap
import com.example.model.MapRoom
import com.example.model.MapRooms
import com.example.model.Player
import com.example.model.QuickChatCatalog
import com.example.model.QuickChatCategory
import com.example.model.QuickChatTemplate
import com.example.model.Role
import com.example.model.SlotType
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceCardLighter
import com.example.ui.theme.SpaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickChatModalSheet(
    currentUserRole: Role,
    players: List<Player>,
    currentMap: GameMap,
    onSendMessage: (text: String, category: QuickChatCategory, isDev: Boolean, isJudge: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf(QuickChatCategory.QUESTIONS) }

    // Template selection state
    var activeTemplate by remember { mutableStateOf<QuickChatTemplate?>(null) }
    var selectedTargetPlayer by remember {
        mutableStateOf(players.firstOrNull { !it.isUser } ?: players.firstOrNull())
    }
    var selectedSecondPlayer by remember {
        mutableStateOf(players.filter { !it.isUser && it.id != selectedTargetPlayer?.id }.firstOrNull())
    }
    val mapRooms = remember(currentMap) { MapRooms.getRoomsForMap(currentMap) }
    var selectedRoom by remember { mutableStateOf(mapRooms.firstOrNull()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SpaceDark,
        tonalElevation = 12.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SpaceBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "💬 Chat Rápido da Reunião",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Frases estruturadas e amigáveis para dedução",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = TextSecondary
                    )
                }
            }

            // Quick 1-Tap Presets Bar
            Text(
                text = "⚡ RESPOSTAS RÁPIDAS (1-TOQUE)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickChatCatalog.instantPresets.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SpaceCard)
                            .border(1.dp, SpaceBorder, RoundedCornerShape(20.dp))
                            .clickable {
                                onSendMessage(preset, QuickChatCategory.QUESTIONS, false, false)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = preset,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedCategory.ordinal,
                containerColor = SpaceDark,
                contentColor = CyanAccent,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedCategory.ordinal]),
                        color = Color(selectedCategory.colorHex)
                    )
                },
                divider = { Box(Modifier.fillMaxWidth().height(1.dp).background(SpaceBorder)) }
            ) {
                QuickChatCategory.values().forEach { cat ->
                    val isCatSelected = selectedCategory == cat
                    Tab(
                        selected = isCatSelected,
                        onClick = {
                            selectedCategory = cat
                            activeTemplate = null // reset template builder
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(cat.icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    cat.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCatSelected) Color(cat.colorHex) else TextSecondary
                                )
                            }
                        }
                    )
                }
            }

            // If a template is active, show the Dynamic Slot Picker + Live Preview
            AnimatedVisibility(visible = activeTemplate != null) {
                val template = activeTemplate
                if (template != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SpaceCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .border(1.5.dp, Color(template.category.colorHex).copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🛠️ Personalizar Frase",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(template.category.colorHex)
                                )
                                IconButton(
                                    onClick = { activeTemplate = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = TextMuted)
                                }
                            }

                            // Slot 1: Target Player Picker
                            if (template.slotType == SlotType.TARGET_PLAYER ||
                                template.slotType == SlotType.TARGET_PLAYER_AND_ROOM ||
                                template.slotType == SlotType.TWO_PLAYERS_AND_ROOM
                            ) {
                                Text(
                                    text = "Escolher Jogador:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    players.forEach { p ->
                                        val isSel = selectedTargetPlayer?.id == p.id
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { selectedTargetPlayer = p },
                                            label = {
                                                Text(
                                                    "${p.colorInfo.name} ${p.hatEmoji}",
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = p.colorInfo.color.copy(alpha = 0.35f),
                                                selectedLabelColor = TextPrimary,
                                                containerColor = SpaceCardLighter,
                                                labelColor = TextSecondary
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                borderColor = if (isSel) p.colorInfo.color else SpaceBorder,
                                                selectedBorderColor = p.colorInfo.color,
                                                enabled = true,
                                                selected = isSel
                                            )
                                        )
                                    }
                                }
                            }

                            // Slot 2: Second Player Picker (for defense / alibi)
                            if (template.slotType == SlotType.TWO_PLAYERS_AND_ROOM) {
                                Text(
                                    text = "Com quem você estava?",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    players.filter { it.id != selectedTargetPlayer?.id }.forEach { p ->
                                        val isSel = selectedSecondPlayer?.id == p.id
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { selectedSecondPlayer = p },
                                            label = {
                                                Text(
                                                    "${p.colorInfo.name} ${p.hatEmoji}",
                                                    fontSize = 12.sp
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = p.colorInfo.color.copy(alpha = 0.35f),
                                                selectedLabelColor = TextPrimary,
                                                containerColor = SpaceCardLighter,
                                                labelColor = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }

                            // Slot 3: Room / Location Picker
                            if (template.slotType == SlotType.ROOM ||
                                template.slotType == SlotType.TARGET_PLAYER_AND_ROOM ||
                                template.slotType == SlotType.TWO_PLAYERS_AND_ROOM
                            ) {
                                Text(
                                    text = "Local (${currentMap.mapName}):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    mapRooms.forEach { room ->
                                        val isSel = selectedRoom?.id == room.id
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { selectedRoom = room },
                                            label = {
                                                Text("${room.icon} ${room.name}", fontSize = 11.sp)
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CyanAccent.copy(alpha = 0.25f),
                                                selectedLabelColor = CyanAccent,
                                                containerColor = SpaceCardLighter,
                                                labelColor = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }

                            // Generated Preview Box
                            val finalPreview = template.buildMessage(
                                targetPlayer = selectedTargetPlayer,
                                secondPlayer = selectedSecondPlayer,
                                room = selectedRoom
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SpaceDark)
                                    .border(1.dp, SpaceBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Prévia da Mensagem:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = finalPreview,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Send Button
                            Button(
                                onClick = {
                                    val isDev = template.category == QuickChatCategory.DEV_ADMIN
                                    val isJudge = template.id == "r_judge_hammer"
                                    onSendMessage(finalPreview, template.category, isDev, isJudge)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(template.category.colorHex)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = SpaceDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enviar no Chat da Reunião",
                                    fontWeight = FontWeight.Bold,
                                    color = SpaceDark,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // List of templates for selected category
            val categoryTemplates = remember(selectedCategory, currentUserRole) {
                QuickChatCatalog.templates.filter { t ->
                    t.category == selectedCategory &&
                    (t.roleRequired == null || t.roleRequired == currentUserRole || currentUserRole == Role.DEV || currentUserRole == Role.ADMIN)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 340.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryTemplates, key = { it.id }) { template ->
                    val isSelected = activeTemplate?.id == template.id
                    val isRoleSpecific = template.roleRequired != null

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SpaceCardLighter else SpaceCard)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) Color(template.category.colorHex) else SpaceBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (template.slotType == SlotType.NONE) {
                                    // Send immediately!
                                    val isDev = template.category == QuickChatCategory.DEV_ADMIN
                                    val isJudge = template.id == "r_judge_hammer"
                                    onSendMessage(template.templateText, template.category, isDev, isJudge)
                                    onDismiss()
                                } else {
                                    // Open slot customizer
                                    activeTemplate = template
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = template.previewEmoji,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            if (isRoleSpecific && template.roleRequired != null) {
                                Text(
                                    text = "HABILIDADE EXCLUSIVA: ${template.roleRequired.displayName.uppercase()}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = template.roleRequired.badgeColor
                                )
                            }
                            Text(
                                text = template.templateText,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        if (template.slotType != SlotType.NONE) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SpaceDark)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Preencher ✏️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
