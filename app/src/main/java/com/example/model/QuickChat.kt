package com.example.model

enum class QuickChatCategory(
    val title: String,
    val icon: String,
    val colorHex: Long
) {
    QUESTIONS("Perguntas", "❓", 0xFF00E5FF),
    SUSPICIONS("Suspeitas", "🔎", 0xFFFF5252),
    DEFENSE("Defesa", "🛡️", 0xFF69F0AE),
    VOTING("Votação", "🗳️", 0xFFFFD166),
    ROLES("Papéis", "⭐", 0xFFA78BFA),
    DEV_ADMIN("Dev & Mod", "💻", 0xFF6366F1)
}

enum class SlotType {
    NONE,
    TARGET_PLAYER,
    ROOM,
    TARGET_PLAYER_AND_ROOM,
    TWO_PLAYERS_AND_ROOM
}

data class QuickChatTemplate(
    val id: String,
    val category: QuickChatCategory,
    val templateText: String,
    val slotType: SlotType,
    val roleRequired: Role? = null,
    val previewEmoji: String = "💬"
) {
    fun buildMessage(targetPlayer: Player? = null, secondPlayer: Player? = null, room: MapRoom? = null): String {
        var text = templateText
        if (targetPlayer != null) {
            text = text.replace("{jogador}", targetPlayer.name)
        }
        if (secondPlayer != null) {
            text = text.replace("{segundo_jogador}", secondPlayer.name)
        }
        if (room != null) {
            text = text.replace("{local}", "${room.icon} ${room.name}")
        }
        return text
    }
}

data class QuickChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderColor: PlayerColor,
    val text: String,
    val category: QuickChatCategory,
    val timestamp: Long = System.currentTimeMillis(),
    val isDevCommand: Boolean = false,
    val isJudgeVerdict: Boolean = false,
    val isSystemAlert: Boolean = false,
    val senderHat: String = "🧢"
)

object QuickChatCatalog {
    val templates = listOf(
        // PERGUNTAS
        QuickChatTemplate(
            id = "q_where_was",
            category = QuickChatCategory.QUESTIONS,
            templateText = "Onde todo mundo estava?",
            slotType = SlotType.NONE,
            previewEmoji = "📍"
        ),
        QuickChatTemplate(
            id = "q_who_saw",
            category = QuickChatCategory.QUESTIONS,
            templateText = "Alguém viu o corpo de pelúcia na {local}?",
            slotType = SlotType.ROOM,
            previewEmoji = "🧸"
        ),
        QuickChatTemplate(
            id = "q_seen_player",
            category = QuickChatCategory.QUESTIONS,
            templateText = "Alguém viu o {jogador} nos últimos momentos?",
            slotType = SlotType.TARGET_PLAYER,
            previewEmoji = "👀"
        ),
        QuickChatTemplate(
            id = "q_who_with_player",
            category = QuickChatCategory.QUESTIONS,
            templateText = "Quem estava acompanhando o {jogador}?",
            slotType = SlotType.TARGET_PLAYER,
            previewEmoji = "👥"
        ),
        QuickChatTemplate(
            id = "q_near_room",
            category = QuickChatCategory.QUESTIONS,
            templateText = "Quem estava passando perto da {local}?",
            slotType = SlotType.ROOM,
            previewEmoji = "🚪"
        ),

        // SUSPEITAS
        QuickChatTemplate(
            id = "s_vent_suspect",
            category = QuickChatCategory.SUSPICIONS,
            templateText = "Eu vi o {jogador} saindo do duto na {local}!",
            slotType = SlotType.TARGET_PLAYER_AND_ROOM,
            previewEmoji = "💨"
        ),
        QuickChatTemplate(
            id = "s_running_away",
            category = QuickChatCategory.SUSPICIONS,
            templateText = "Vi o {jogador} correndo na direção oposta da {local}!",
            slotType = SlotType.TARGET_PLAYER_AND_ROOM,
            previewEmoji = "🏃"
        ),
        QuickChatTemplate(
            id = "s_quiet",
            category = QuickChatCategory.SUSPICIONS,
            templateText = "Acho que o {jogador} está muito quieto e suspeito...",
            slotType = SlotType.TARGET_PLAYER,
            previewEmoji = "🤫"
        ),
        QuickChatTemplate(
            id = "s_fake_task",
            category = QuickChatCategory.SUSPICIONS,
            templateText = "O {jogador} fingiu fazer uma tarefa na {local}!",
            slotType = SlotType.TARGET_PLAYER_AND_ROOM,
            previewEmoji = "❌"
        ),
        QuickChatTemplate(
            id = "s_following_me",
            category = QuickChatCategory.SUSPICIONS,
            templateText = "O {jogador} estava me seguindo na {local}!",
            slotType = SlotType.TARGET_PLAYER_AND_ROOM,
            previewEmoji = "👣"
        ),
        QuickChatTemplate(
            id = "s_shapeshifter",
            category = QuickChatCategory.SUSPICIONS,
            templateText = "Cuidado! Pode ser o Metamorfo disfarçado de {jogador}!",
            slotType = SlotType.TARGET_PLAYER,
            previewEmoji = "🎭"
        ),

        // DEFESAS
        QuickChatTemplate(
            id = "d_with_friend",
            category = QuickChatCategory.DEFENSE,
            templateText = "Eu estava fazendo tarefas na {local} junto com {segundo_jogador}!",
            slotType = SlotType.TWO_PLAYERS_AND_ROOM,
            previewEmoji = "🤝"
        ),
        QuickChatTemplate(
            id = "d_innocent_trust",
            category = QuickChatCategory.DEFENSE,
            templateText = "Eu sou 100% inocente! Confiem em mim!",
            slotType = SlotType.NONE,
            previewEmoji = "😇"
        ),
        QuickChatTemplate(
            id = "d_doing_lights",
            category = QuickChatCategory.DEFENSE,
            templateText = "Eu estava consertando as luzes e fiação na {local}.",
            slotType = SlotType.ROOM,
            previewEmoji = "💡"
        ),
        QuickChatTemplate(
            id = "d_visual_task",
            category = QuickChatCategory.DEFENSE,
            templateText = "Eu tenho uma tarefa visual na {local} para provar minha inocência!",
            slotType = SlotType.ROOM,
            previewEmoji = "✨"
        ),
        QuickChatTemplate(
            id = "d_player_can_confirm",
            category = QuickChatCategory.DEFENSE,
            templateText = "Não fui eu! O {jogador} pode confirmar meu álibi!",
            slotType = SlotType.TARGET_PLAYER,
            previewEmoji = "🙋"
        ),

        // VOTAÇÃO & ESTRATÉGIA
        QuickChatTemplate(
            id = "v_skip",
            category = QuickChatCategory.VOTING,
            templateText = "Vamos pular o voto nessa rodada (Skip)! Ainda não temos provas.",
            slotType = SlotType.NONE,
            previewEmoji = "⏭️"
        ),
        QuickChatTemplate(
            id = "v_vote_target",
            category = QuickChatCategory.VOTING,
            templateText = "Vamos votar no {jogador}! As pistas apontam para ele.",
            slotType = SlotType.TARGET_PLAYER,
            previewEmoji = "🗳️"
        ),
        QuickChatTemplate(
            id = "v_if_not_vote_me",
            category = QuickChatCategory.VOTING,
            templateText = "Se não for o {jogador}, podem votar em mim na próxima reunião!",
            slotType = SlotType.TARGET_PLAYER,
            previewEmoji = "⚖️"
        ),
        QuickChatTemplate(
            id = "v_tasks_almost_done",
            category = QuickChatCategory.VOTING,
            templateText = "Faltam pouquíssimas tarefas! Vamos focar em terminar logo!",
            slotType = SlotType.NONE,
            previewEmoji = "🚀"
        ),
        QuickChatTemplate(
            id = "v_hurry_time",
            category = QuickChatCategory.VOTING,
            templateText = "Atenção ao tempo da reunião! Votem antes que expire!",
            slotType = SlotType.NONE,
            previewEmoji = "⏱️"
        ),

        // PAPÉIS ESPECIAIS
        QuickChatTemplate(
            id = "r_judge_hammer",
            category = QuickChatCategory.ROLES,
            templateText = "🔨 [Juiz] Sentença Final! Usando o Martelo do Juízo no {jogador}!",
            slotType = SlotType.TARGET_PLAYER,
            roleRequired = Role.JUIZ,
            previewEmoji = "🔨"
        ),
        QuickChatTemplate(
            id = "r_shield_protect",
            category = QuickChatCategory.ROLES,
            templateText = "🛡️ [Protetor] Eu colei um adesivo de escudo no {jogador}!",
            slotType = SlotType.TARGET_PLAYER,
            roleRequired = Role.PROTETOR,
            previewEmoji = "🛡️"
        ),
        QuickChatTemplate(
            id = "r_sheriff_eye",
            category = QuickChatCategory.ROLES,
            templateText = "⭐ [Xerife] Minha arminha d'água está pronta. De olho no {jogador}!",
            slotType = SlotType.TARGET_PLAYER,
            roleRequired = Role.XERIFE,
            previewEmoji = "⭐"
        ),
        QuickChatTemplate(
            id = "r_cams_info",
            category = QuickChatCategory.ROLES,
            templateText = "📹 [Câmerador] Pelo monitor vi movimentação suspeita na {local}!",
            slotType = SlotType.ROOM,
            roleRequired = Role.CAMERADOR,
            previewEmoji = "📹"
        ),
        QuickChatTemplate(
            id = "r_mechanic_vent",
            category = QuickChatCategory.ROLES,
            templateText = "🔧 [Mecânico] Espiei os dutos da {local}, tudo verificado!",
            slotType = SlotType.ROOM,
            roleRequired = Role.MECANICO,
            previewEmoji = "🔧"
        ),
        QuickChatTemplate(
            id = "r_medic_revive",
            category = QuickChatCategory.ROLES,
            templateText = "🩺 [Médico] Monitorei os sinais vitais e salvei um aliado na {local}!",
            slotType = SlotType.ROOM,
            roleRequired = Role.MEDICO,
            previewEmoji = "🩺"
        ),
        QuickChatTemplate(
            id = "r_time_warp",
            category = QuickChatCategory.ROLES,
            templateText = "⏳ [Cronometrista] Relógio quântico carregado para a próxima rodada!",
            slotType = SlotType.NONE,
            roleRequired = Role.CRONOMETRISTA,
            previewEmoji = "⏳"
        ),

        // DEV & ADMIN
        QuickChatTemplate(
            id = "dev_syntax_error",
            category = QuickChatCategory.DEV_ADMIN,
            templateText = "💻 [Dev] Disparando >> SyntaxError: Unexpected impostor token no chat!",
            slotType = SlotType.NONE,
            roleRequired = Role.DEV,
            previewEmoji = "⚠️"
        ),
        QuickChatTemplate(
            id = "dev_lag_spike",
            category = QuickChatCategory.DEV_ADMIN,
            templateText = "⚡ [Dev] Injetando Lag Spike no servidor (Ping: 999ms)!",
            slotType = SlotType.NONE,
            roleRequired = Role.DEV,
            previewEmoji = "⚡"
        ),
        QuickChatTemplate(
            id = "dev_shutdown",
            category = QuickChatCategory.DEV_ADMIN,
            templateText = "🛑 [Dev] Executando >> Player.shutdown(10s) no {jogador}!",
            slotType = SlotType.TARGET_PLAYER,
            roleRequired = Role.DEV,
            previewEmoji = "🛑"
        ),
        QuickChatTemplate(
            id = "admin_broadcast",
            category = QuickChatCategory.DEV_ADMIN,
            templateText = "📢 [Admin] ALERTA GERAL: A discussão deve ser amigável e esportiva!",
            slotType = SlotType.NONE,
            roleRequired = Role.ADMIN,
            previewEmoji = "📢"
        )
    )

    // Quick 1-tap presets that don't need any parameter selection
    val instantPresets = listOf(
        "Onde foi? 📍",
        "Quem viu algo? 🧐",
        "Eu sou inocente! 😇",
        "Pular voto (Skip)? ⏭️",
        "Confiem em mim! 🤝",
        "Muito suspeito! 🤨"
    )
}
