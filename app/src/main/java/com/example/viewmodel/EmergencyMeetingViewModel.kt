package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.GameMap
import com.example.model.MapRooms
import com.example.model.Player
import com.example.model.PlayerColorPalette
import com.example.model.QuickChatCatalog
import com.example.model.QuickChatCategory
import com.example.model.QuickChatMessage
import com.example.model.Role
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

data class MeetingUiState(
    val currentMap: GameMap = GameMap.SPACE_SCHOOL,
    val players: List<Player> = emptyList(),
    val meetingCaller: Player? = null,
    val isBodyReport: Boolean = false,
    val reportedRoomName: String? = null,
    val secondsRemaining: Int = 60,
    val isTimerRunning: Boolean = true,
    val messages: List<QuickChatMessage> = emptyList(),
    val isQuickChatSheetOpen: Boolean = false,
    val activeTab: MeetingTab = MeetingTab.CHAT,
    val verdictTitle: String? = null,
    val verdictMessage: String? = null,
    val isVotingConcluded: Boolean = false,
    val userRole: Role = Role.INOCENTE
)

enum class MeetingTab {
    CHAT,
    VOTING
}

class EmergencyMeetingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MeetingUiState())
    val uiState: StateFlow<MeetingUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var simulatedChatJob: Job? = null
    private var simulatedVotingJob: Job? = null

    init {
        startNewMeeting()
    }

    fun startNewMeeting(
        userRole: Role = Role.INOCENTE,
        map: GameMap = GameMap.SPACE_SCHOOL,
        callerName: String? = null,
        isBodyReport: Boolean = false,
        reportedRoom: String? = null
    ) {
        timerJob?.cancel()
        simulatedChatJob?.cancel()
        simulatedVotingJob?.cancel()

        val hats = listOf("👑", "🌸", "🎩", "🧢", "🌿", "🎀", "🎧", "🍦", "🚀", "⭐", "🤠", "🤖")
        val allColors = PlayerColorPalette.colors.shuffled()

        // Assign roles to 10 players
        val playerRoles = mutableListOf(
            userRole,
            Role.IMPOSTOR,
            Role.METAMORFO,
            Role.XERIFE,
            Role.JUIZ,
            Role.PROTETOR,
            Role.MECANICO,
            Role.CAMERADOR,
            Role.MEDICO,
            Role.CRONOMETRISTA
        )
        // User gets userRole
        playerRoles.remove(userRole)
        playerRoles.add(0, userRole)

        val playerList = allColors.take(10).mapIndexed { index, colorInfo ->
            val isUser = index == 0
            val role = playerRoles[index]
            Player(
                id = "player_$index",
                name = if (isUser) "Você (${colorInfo.name})" else colorInfo.name,
                colorInfo = colorInfo,
                role = role,
                isUser = isUser,
                isAlive = index != 7, // 1 player dead as cute plushie body to report
                isProtected = index == 2, // 1 player protected with shield sticker
                hasVoted = false,
                hatEmoji = hats[index % hats.size]
            )
        }

        val isBody = isBodyReport
        val caller = if (callerName != null && callerName == "Você") {
            playerList.first { it.isUser }
        } else {
            playerList.filter { it.isAlive && !it.isUser }.random()
        }
        val rooms = MapRooms.getRoomsForMap(map)
        val selectedRoom = rooms.firstOrNull { it.name == reportedRoom } ?: rooms.random()
        val roomDisplay = "${selectedRoom.icon} ${selectedRoom.name}"

        val initialMessages = mutableListOf<QuickChatMessage>()

        if (isBody) {
            initialMessages.add(
                QuickChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderId = caller.id,
                    senderName = caller.name,
                    senderColor = caller.colorInfo,
                    text = "🚨 Encontrei uma pelúcia de tripulante na $roomDisplay!",
                    category = QuickChatCategory.QUESTIONS,
                    senderHat = caller.hatEmoji
                )
            )
        } else {
            initialMessages.add(
                QuickChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderId = caller.id,
                    senderName = caller.name,
                    senderColor = caller.colorInfo,
                    text = "📢 Apertei o botão de emergência! Precisamos conversar sobre algo suspeito.",
                    category = QuickChatCategory.QUESTIONS,
                    senderHat = caller.hatEmoji
                )
            )
        }

        _uiState.value = MeetingUiState(
            currentMap = map,
            players = playerList,
            meetingCaller = caller,
            isBodyReport = isBody,
            reportedRoomName = roomDisplay,

            secondsRemaining = 60,
            isTimerRunning = true,
            messages = initialMessages,
            isQuickChatSheetOpen = false,
            activeTab = MeetingTab.CHAT,
            verdictTitle = null,
            verdictMessage = null,
            isVotingConcluded = false,
            userRole = userRole
        )

        startTimer()
        startSimulatedDiscussion()
        startSimulatedVotes()
    }

    fun switchUserRole(newRole: Role) {
        _uiState.update { state ->
            val updatedPlayers = state.players.map { player ->
                if (player.isUser) player.copy(role = newRole) else player
            }
            state.copy(userRole = newRole, players = updatedPlayers)
        }

        // Post role announcement in chat
        sendSystemMessage("O jogador alterou sua função para ${newRole.icon} ${newRole.displayName} para teste das mecânicas!")
    }

    fun switchMap(newMap: GameMap) {
        startNewMeeting(userRole = _uiState.value.userRole, map = newMap)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.secondsRemaining > 0 && !_uiState.value.isVotingConcluded) {
                delay(1000L)
                _uiState.update { it.copy(secondsRemaining = it.secondsRemaining - 1) }
            }
            if (!_uiState.value.isVotingConcluded) {
                concludeVoting()
            }
        }
    }

    private fun startSimulatedDiscussion() {
        simulatedChatJob?.cancel()
        simulatedChatJob = viewModelScope.launch {
            val aliveBots = _uiState.value.players.filter { !it.isUser && it.isAlive }
            val rooms = MapRooms.getRoomsForMap(_uiState.value.currentMap)

            val botDialogues = listOf(
                "Onde foi isso? Eu estava fazendo tarefas!",
                "Eu vi alguém correndo perto da ${rooms.firstOrNull()?.name ?: "Cafeteria"}!",
                "Eu sou inocente, confiem em mim!",
                "Quem mais estava perto daquele local?",
                "Eu estava consertando as luzes, não vi nada.",
                "Acho que o Vermelhinho está muito quieto...",
                "Vamos pular o voto se não tivermos certeza (Skip)!",
                "Se não tivermos provas, é melhor não arriscar."
            )

            var dialogueIdx = 0
            while (!_uiState.value.isVotingConcluded && dialogueIdx < botDialogues.size) {
                delay(Random.nextLong(3500L, 7000L))
                if (_uiState.value.isVotingConcluded) break

                val speaker = aliveBots.random()
                val phrase = botDialogues[dialogueIdx % botDialogues.size]
                dialogueIdx++

                val newMsg = QuickChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderId = speaker.id,
                    senderName = speaker.name,
                    senderColor = speaker.colorInfo,
                    text = phrase,
                    category = if (phrase.contains("?")) QuickChatCategory.QUESTIONS else QuickChatCategory.SUSPICIONS,
                    senderHat = speaker.hatEmoji
                )

                _uiState.update { it.copy(messages = it.messages + newMsg) }
            }
        }
    }

    private fun startSimulatedVotes() {
        simulatedVotingJob?.cancel()
        simulatedVotingJob = viewModelScope.launch {
            val aliveBots = _uiState.value.players.filter { !it.isUser && it.isAlive }

            for (bot in aliveBots) {
                delay(Random.nextLong(4000L, 9000L))
                if (_uiState.value.isVotingConcluded) break

                _uiState.update { state ->
                    val updated = state.players.map {
                        if (it.id == bot.id) it.copy(hasVoted = true) else it
                    }
                    state.copy(players = updated)
                }

                // Check if all players have voted
                val allVoted = _uiState.value.players.filter { it.isAlive }.all { it.hasVoted }
                if (allVoted) {
                    concludeVoting()
                    break
                }
            }
        }
    }

    fun sendQuickChatMessage(
        text: String,
        category: QuickChatCategory,
        isDevCommand: Boolean = false,
        isJudgeHammer: Boolean = false
    ) {
        val currentUser = _uiState.value.players.firstOrNull { it.isUser } ?: return

        val msg = QuickChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = currentUser.id,
            senderName = currentUser.name,
            senderColor = currentUser.colorInfo,
            text = text,
            category = category,
            isDevCommand = isDevCommand,
            isJudgeVerdict = isJudgeHammer,
            senderHat = currentUser.hatEmoji
        )

        _uiState.update { it.copy(messages = it.messages + msg) }

        // If it's a dev command, simulate reactive bot response
        if (isDevCommand) {
            viewModelScope.launch {
                delay(1200L)
                val bot = _uiState.value.players.filter { !it.isUser && it.isAlive }.randomOrNull()
                if (bot != null) {
                    val reply = QuickChatMessage(
                        id = UUID.randomUUID().toString(),
                        senderId = bot.id,
                        senderName = bot.name,
                        senderColor = bot.colorInfo,
                        text = "😮 O Dev usou um comando no sistema! O que aconteceu com a conexão?",
                        category = QuickChatCategory.QUESTIONS,
                        senderHat = bot.hatEmoji
                    )
                    _uiState.update { it.copy(messages = it.messages + reply) }
                }
            }
        }
    }

    private fun sendSystemMessage(text: String) {
        val sysMsg = QuickChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = "system",
            senderName = "Sistema Among Kids",
            senderColor = PlayerColorPalette.colors[1],
            text = text,
            category = QuickChatCategory.DEV_ADMIN,
            isSystemAlert = true,
            senderHat = "📢"
        )
        _uiState.update { it.copy(messages = it.messages + sysMsg) }
    }

    fun votePlayer(targetPlayer: Player) {
        _uiState.update { state ->
            val updated = state.players.map {
                if (it.isUser) it.copy(hasVoted = true, votedForPlayerId = targetPlayer.id) else it
            }
            state.copy(players = updated)
        }

        sendQuickChatMessage(
            text = "Meu voto foi para ${targetPlayer.name}!",
            category = QuickChatCategory.VOTING
        )

        checkIfAllVoted()
    }

    fun skipVote() {
        _uiState.update { state ->
            val updated = state.players.map {
                if (it.isUser) it.copy(hasVoted = true, isSkippedVote = true) else it
            }
            state.copy(players = updated)
        }

        sendQuickChatMessage(
            text = "Decidi pular meu voto (Skip)!",
            category = QuickChatCategory.VOTING
        )

        checkIfAllVoted()
    }

    fun judgeInstantVerdict(target: Player) {
        val isImpostor = target.isImpostor

        sendQuickChatMessage(
            text = "🔨 [JUIZ] Bati o Martelo do Juízo no ${target.name}!",
            category = QuickChatCategory.ROLES,
            isJudgeHammer = true
        )

        viewModelScope.launch {
            delay(1500L)
            if (isImpostor) {
                _uiState.update {
                    it.copy(
                        isVotingConcluded = true,
                        verdictTitle = "🏆 VITÓRIA DOS INOCENTES!",
                        verdictMessage = "O Juiz acertou em cheio! ${target.name} era o Impostor real (${target.role.displayName}). Todos os outros votos foram anulados pelo Martelo da Justiça!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isVotingConcluded = true,
                        verdictTitle = "❌ O JUIZ ERROU!",
                        verdictMessage = "O Martelo atingiu um Inocente! ${target.name} não era impostor. Os impostores venceram a rodada com a confusão gerada!"
                    )
                }
            }
        }
    }

    private fun checkIfAllVoted() {
        val allVoted = _uiState.value.players.filter { it.isAlive }.all { it.hasVoted }
        if (allVoted) {
            concludeVoting()
        }
    }

    private fun concludeVoting() {
        timerJob?.cancel()
        simulatedChatJob?.cancel()

        val alivePlayers = _uiState.value.players.filter { it.isAlive }
        val randomEjected = alivePlayers.filter { !it.isUser }.randomOrNull()

        val title: String
        val desc: String

        if (Random.nextBoolean() && randomEjected != null) {
            val wasImpostor = randomEjected.isImpostor
            title = "${randomEjected.name} foi colocado na nave de descanso!"
            desc = if (wasImpostor) {
                "Ele era um Impostor (${randomEjected.role.displayName})! Resta apenas 1 Impostor no jogo."
            } else {
                "Ele era Inocente (${randomEjected.role.displayName}). Nenhum impostor foi ejetado."
            }
        } else {
            title = "Ninguém foi ejetado! (Voto Pulado)"
            desc = "A maioria dos tripulantes decidiu pular o voto (Skip). O jogo continua!"
        }

        _uiState.update {
            it.copy(
                isVotingConcluded = true,
                verdictTitle = title,
                verdictMessage = desc
            )
        }
    }

    fun openQuickChat() {
        _uiState.update { it.copy(isQuickChatSheetOpen = true) }
    }

    fun closeQuickChat() {
        _uiState.update { it.copy(isQuickChatSheetOpen = false) }
    }

    fun setTab(tab: MeetingTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }
}
