package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.PlayerBlack
import com.example.ui.theme.PlayerBlue
import com.example.ui.theme.PlayerBrown
import com.example.ui.theme.PlayerCyan
import com.example.ui.theme.PlayerGreen
import com.example.ui.theme.PlayerLime
import com.example.ui.theme.PlayerOrange
import com.example.ui.theme.PlayerPink
import com.example.ui.theme.PlayerPurple
import com.example.ui.theme.PlayerRed
import com.example.ui.theme.PlayerWhite
import com.example.ui.theme.PlayerYellow

data class PlayerColor(
    val id: String,
    val name: String,
    val color: Color
)

object PlayerColorPalette {
    val colors = listOf(
        PlayerColor("red", "Vermelhinho", PlayerRed),
        PlayerColor("blue", "Azulzinho", PlayerBlue),
        PlayerColor("green", "Verdinho", PlayerGreen),
        PlayerColor("pink", "Rosinha", PlayerPink),
        PlayerColor("orange", "Laranjinha", PlayerOrange),
        PlayerColor("yellow", "Amarelinho", PlayerYellow),
        PlayerColor("purple", "Roxinho", PlayerPurple),
        PlayerColor("cyan", "Ciano", PlayerCyan),
        PlayerColor("lime", "Limãozinho", PlayerLime),
        PlayerColor("white", "Branquinho", PlayerWhite),
        PlayerColor("brown", "Marronzinho", PlayerBrown),
        PlayerColor("black", "Pretinho", PlayerBlack)
    )
}

data class Player(
    val id: String,
    val name: String,
    val colorInfo: PlayerColor,
    val role: Role,
    val isUser: Boolean = false,
    val isAlive: Boolean = true,
    val isProtected: Boolean = false,
    val hasVoted: Boolean = false,
    val votedForPlayerId: String? = null,
    val isSkippedVote: Boolean = false,
    val isSilencedByDev: Boolean = false,
    val hatEmoji: String = "🧢",
    val isMorphed: Boolean = false
) {
    val isImpostor: Boolean
        get() = role == Role.IMPOSTOR || role == Role.METAMORFO
}
