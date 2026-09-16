package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameMap
import com.example.model.Role
import com.example.model.RoleLotteryConfig
import com.example.ui.theme.PlayerCyan
import com.example.viewmodel.EmergencyMeetingViewModel

enum class AppScreen {
    MAIN_MENU,
    GAMEPLAY_MAP,
    EMERGENCY_MEETING
}

@Composable
fun AmongKidsRootApp(
    meetingViewModel: EmergencyMeetingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(AppScreen.MAIN_MENU) }
    var currentRole by remember { mutableStateOf(Role.INOCENTE) }
    var isPracticeMode by remember { mutableStateOf(false) }
    var showInitialReveal by remember { mutableStateOf(false) }

    // Customization & Lottery Configuration
    var playerColor by remember { mutableStateOf(PlayerCyan) }
    var playerHat by remember { mutableStateOf("🧢") }
    var lotteryConfig by remember { mutableStateOf(RoleLotteryConfig(impostorChance = 10)) }

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(durationMillis = 300),
        modifier = modifier.fillMaxSize(),
        label = "screen_crossfade"
    ) { screen ->
        when (screen) {
            AppScreen.MAIN_MENU -> {
                MainMenuScreen(
                    playerColor = playerColor,
                    playerHat = playerHat,
                    lotteryConfig = lotteryConfig,
                    onStartRealMatch = {
                        // 1. Desafio Real: bots se mexendo, sem computador na nave, sorteio de papel!
                        isPracticeMode = false
                        val rolled = lotteryConfig.rollUserRole()
                        currentRole = rolled
                        meetingViewModel.switchUserRole(rolled)
                        showInitialReveal = true
                        currentScreen = AppScreen.GAMEPLAY_MAP
                    },
                    onStartPracticeMode = {
                        // 2. Modo Prática: bots parados, computador na Cafeteria para escolher papel livremente!
                        isPracticeMode = true
                        showInitialReveal = false
                        currentScreen = AppScreen.GAMEPLAY_MAP
                    },
                    onUpdateLotteryConfig = { newConfig ->
                        lotteryConfig = newConfig
                    },
                    onChangeColor = { col ->
                        playerColor = col
                    },
                    onChangeHat = { hat ->
                        playerHat = hat
                    }
                )
            }

            AppScreen.GAMEPLAY_MAP -> {
                GamePlayScreen(
                    currentRole = currentRole,
                    isPracticeMode = isPracticeMode,
                    playerColor = playerColor,
                    playerHat = playerHat,
                    showInitialReveal = showInitialReveal,
                    onReturnToMenu = {
                        currentScreen = AppScreen.MAIN_MENU
                    },
                    onTriggerEmergencyMeeting = { caller, isBody, room ->
                        meetingViewModel.startNewMeeting(
                            userRole = currentRole,
                            map = GameMap.SPACE_SCHOOL,
                            callerName = caller,
                            isBodyReport = isBody,
                            reportedRoom = room
                        )
                        currentScreen = AppScreen.EMERGENCY_MEETING
                    },
                    onChangeRole = { newRole ->
                        currentRole = newRole
                        meetingViewModel.switchUserRole(newRole)
                    }
                )
            }

            AppScreen.EMERGENCY_MEETING -> {
                EmergencyMeetingScreen(
                    viewModel = meetingViewModel,
                    onReturnToShip = {
                        showInitialReveal = false
                        currentScreen = AppScreen.GAMEPLAY_MAP
                    }
                )
            }
        }
    }
}
