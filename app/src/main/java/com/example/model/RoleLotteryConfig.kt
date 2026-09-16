package com.example.model

import kotlin.random.Random

data class RoleLotteryConfig(
    val impostorChance: Int = 10, // 10% padrão pedido pelo usuário
    val enableSpecialRoles: Boolean = false,
    val xerifeChance: Int = 10,
    val mecanicoChance: Int = 10,
    val metamorfoChance: Int = 5
) {
    /**
     * Sorteia o papel do jogador com base nas chances configuradas (Sorte vs Azar).
     * Sorte de vir Impostor (10% padrão), ou azar de vir Inocente (90%).
     */
    fun rollUserRole(): Role {
        val dice = Random.nextInt(1, 101) // 1 a 100

        // 1. Checa se tirou a sorte de ser Impostor
        if (dice <= impostorChance) {
            return if (enableSpecialRoles && Random.nextBoolean()) Role.METAMORFO else Role.IMPOSTOR
        }

        // 2. Se papéis especiais estiverem ativos, sorteia entre eles
        if (enableSpecialRoles) {
            val specialDice = Random.nextInt(1, 101)
            if (specialDice <= xerifeChance) return Role.XERIFE
            if (specialDice <= xerifeChance + mecanicoChance) return Role.MECANICO
        }

        // 3. Caso contrário, deu azar: virou Inocente!
        return Role.INOCENTE
    }
}
