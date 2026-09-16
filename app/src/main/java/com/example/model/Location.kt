package com.example.model

enum class GameMap(val mapName: String, val icon: String) {
    SPACE_SCHOOL("Escola Espacial", "🚀"),
    NEON_PARK("Parque Neon", "🎡"),
    TOY_FACTORY("Fábrica de Brinquedos", "🧸")
}

data class MapRoom(
    val id: String,
    val name: String,
    val icon: String,
    val map: GameMap
)

object MapRooms {
    val allRooms = listOf(
        // Escola Espacial
        MapRoom("cafeteria", "Cafeteria Central", "☕", GameMap.SPACE_SCHOOL),
        MapRoom("eletrica", "Sala Elétrica", "⚡", GameMap.SPACE_SCHOOL),
        MapRoom("laboratorio", "Laboratório de Ciências", "🧪", GameMap.SPACE_SCHOOL),
        MapRoom("motores", "Motores Espaciais", "🚀", GameMap.SPACE_SCHOOL),
        MapRoom("cameras", "Sala de Segurança", "📹", GameMap.SPACE_SCHOOL),
        MapRoom("escudo", "Sala de Escudos", "🛡️", GameMap.SPACE_SCHOOL),
        MapRoom("navegacao", "Navegação Estelar", "🧭", GameMap.SPACE_SCHOOL),

        // Parque Neon
        MapRoom("arcade", "Arena Arcade", "🕹️", GameMap.NEON_PARK),
        MapRoom("montanha_russa", "Montanha-Russa Neon", "🎢", GameMap.NEON_PARK),
        MapRoom("roda_gigante", "Roda Gigante de Luzes", "🎡", GameMap.NEON_PARK),
        MapRoom("pista_danca", "Pista de Dança LED", "🪩", GameMap.NEON_PARK),

        // Fábrica de Brinquedos
        MapRoom("linha_montagem", "Linha de Pelúcias", "🧸", GameMap.TOY_FACTORY),
        MapRoom("sala_presentes", "Embrulho de Presentes", "🎁", GameMap.TOY_FACTORY),
        MapRoom("esteira", "Esteira de Robôs", "🤖", GameMap.TOY_FACTORY),
        MapRoom("deposito", "Depósito de Blocos", "🧱", GameMap.TOY_FACTORY)
    )

    fun getRoomsForMap(map: GameMap): List<MapRoom> {
        return allRooms.filter { it.map == map }
    }
}
