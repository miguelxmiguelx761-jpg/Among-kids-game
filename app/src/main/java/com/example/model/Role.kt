package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PlayerBlue
import com.example.ui.theme.PlayerCyan
import com.example.ui.theme.PlayerLime
import com.example.ui.theme.PlayerOrange
import com.example.ui.theme.PlayerPink
import com.example.ui.theme.PlayerPurple
import com.example.ui.theme.PlayerRed
import com.example.ui.theme.PlayerYellow
import com.example.ui.theme.SoftPurple

enum class Team {
    CREWMATE,
    IMPOSTOR,
    SPECIAL_ADMIN
}

enum class Role(
    val displayName: String,
    val team: Team,
    val icon: String,
    val description: String,
    val badgeColor: Color,
    val meetingAbilityName: String? = null
) {
    INOCENTE(
        displayName = "Inocente",
        team = Team.CREWMATE,
        icon = "🚀",
        description = "Complete tarefas e use o Chat Rápido nas reuniões para descobrir os impostores.",
        badgeColor = PlayerBlue
    ),
    XERIFE(
        displayName = "Xerife",
        team = Team.CREWMATE,
        icon = "⭐",
        description = "Equipado com arminha d'água para neutralizar o Impostor. Cuidado para não errar!",
        badgeColor = AmberGold
    ),
    JUIZ(
        displayName = "Juiz",
        team = Team.CREWMATE,
        icon = "⚖️",
        description = "Possui o Martelo do Juízo na reunião. Se acusar o impostor real, decreta vitória imediata!",
        badgeColor = SoftPurple,
        meetingAbilityName = "Martelo do Juízo 🔨"
    ),
    PROTETOR(
        displayName = "Protetor",
        team = Team.CREWMATE,
        icon = "🛡️",
        description = "Coloca adesivos de escudo em aliados, absorvendo ataques de pelúcia.",
        badgeColor = CyanAccent
    ),
    CAMERADOR(
        displayName = "Câmerador",
        team = Team.CREWMATE,
        icon = "📸",
        description = "Tablet de câmeras com visualização contínua das salas.",
        badgeColor = PlayerPink
    ),
    MECANICO(
        displayName = "Mecânico",
        team = Team.CREWMATE,
        icon = "🔧",
        description = "Acesso livre aos dutos e diagnóstico de sabotagens.",
        badgeColor = PlayerOrange
    ),
    MEDICO(
        displayName = "Médico",
        team = Team.CREWMATE,
        icon = "🩺",
        description = "Monitora sinais vitais e pode reviver jogadores de pelúcia caídos.",
        badgeColor = NeonGreen
    ),
    CRONOMETRISTA(
        displayName = "Cronometrista",
        team = Team.CREWMATE,
        icon = "⏳",
        description = "Relógio quântico que paralisa os impostores por 3 segundos.",
        badgeColor = PlayerLime
    ),
    IMPOSTOR(
        displayName = "Impostor",
        team = Team.IMPOSTOR,
        icon = "😈",
        description = "Elimina alvos com fofas pelúcias, sabota e engana nas discussões.",
        badgeColor = CoralRed
    ),
    METAMORFO(
        displayName = "Metamorfo",
        team = Team.IMPOSTOR,
        icon = "🎭",
        description = "Copia a aparência de outros tripulantes e hackeia câmeras.",
        badgeColor = PlayerPurple
    ),
    DEV(
        displayName = "Dev (Programador)",
        team = Team.SPECIAL_ADMIN,
        icon = "💻",
        description = "Injeta comandos de chat como Syntax Error, Lag Spike e Shutdown temporário.",
        badgeColor = DeepIndigo,
        meetingAbilityName = "Console de Comandos 💻"
    ),
    ADMIN(
        displayName = "Admin",
        team = Team.SPECIAL_ADMIN,
        icon = "👑",
        description = "Moderação total da partida, visão expandida e avisos globais.",
        badgeColor = BrightYellow,
        meetingAbilityName = "Aviso Global do Sistema 📢"
    )
}
