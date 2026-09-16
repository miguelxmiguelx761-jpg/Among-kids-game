package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

data class ClassicRoom(
    val id: String,
    val name: String,
    val icon: String,
    val bounds: Rect,
    val colorHex: Long = 0xFF1E293B
)

data class ShipTask(
    val id: String,
    val name: String,
    val room: String,
    val position: Offset,
    val type: TaskType,
    var isCompleted: Boolean = false
)

enum class TaskType {
    WIRES,
    CARD_SWIPE,
    SHIELDS,
    NAVIGATION,
    DOWNLOAD
}

data class ShipVent(
    val id: String,
    val roomName: String,
    val position: Offset,
    val connectedVentIds: List<String>
)

object ClassicTheSkeldMap {
    // Coordinate system: Map is 1200 x 900 units
    val MAP_WIDTH = 1200f
    val MAP_HEIGHT = 900f

    // Cafeteria Emergency Table position (Center Top)
    val EMERGENCY_TABLE_POS = Offset(600f, 240f)

    // Cafeteria Customization Laptop / Computer position
    val LAPTOP_COMPUTER_POS = Offset(500f, 180f)

    val rooms = listOf(
        ClassicRoom("cafeteria", "Cafeteria", "☕", Rect(440f, 120f, 760f, 340f), 0xFF1E293B),
        ClassicRoom("weapons", "Armaria", "🎯", Rect(840f, 130f, 1060f, 290f), 0xFF1E293B),
        ClassicRoom("o2", "Oxigênio (O2)", "🌿", Rect(780f, 320f, 920f, 440f), 0xFF1E293B),
        ClassicRoom("navigation", "Navegação", "🧭", Rect(980f, 370f, 1180f, 540f), 0xFF1E293B),
        ClassicRoom("shields", "Escudos", "🛡️", Rect(840f, 580f, 1040f, 730f), 0xFF1E293B),
        ClassicRoom("comms", "Comunicações", "📡", Rect(660f, 660f, 820f, 790f), 0xFF1E293B),
        ClassicRoom("storage", "Armazenamento", "📦", Rect(480f, 540f, 720f, 780f), 0xFF1E293B),
        ClassicRoom("admin", "Administração", "📋", Rect(680f, 410f, 860f, 550f), 0xFF1E293B),
        ClassicRoom("electrical", "Elétrica", "⚡", Rect(320f, 440f, 500f, 620f), 0xFF1E293B),
        ClassicRoom("lower_engine", "Motor Inferior", "🚀", Rect(140f, 600f, 320f, 780f), 0xFF1E293B),
        ClassicRoom("security", "Segurança", "📹", Rect(220f, 400f, 360f, 520f), 0xFF1E293B),
        ClassicRoom("reactor", "Reator", "☢️", Rect(40f, 360f, 180f, 560f), 0xFF1E293B),
        ClassicRoom("upper_engine", "Motor Superior", "🚀", Rect(140f, 140f, 320f, 320f), 0xFF1E293B),
        ClassicRoom("medbay", "Enfermaria", "🩺", Rect(360f, 260f, 520f, 400f), 0xFF1E293B)
    )

    val corridors = listOf(
        // Cafeteria to Weapons
        Rect(760f, 190f, 840f, 250f),
        // Cafeteria to Admin/Storage
        Rect(570f, 340f, 630f, 540f),
        // Cafeteria to MedBay
        Rect(400f, 200f, 440f, 260f),
        // Upper Engine to Cafeteria
        Rect(320f, 190f, 440f, 250f),
        // Upper Engine to Reactor
        Rect(100f, 220f, 140f, 380f),
        // Lower Engine to Reactor
        Rect(100f, 540f, 140f, 680f),
        // Security Corridor
        Rect(180f, 440f, 220f, 480f),
        Rect(320f, 320f, 360f, 440f),
        // Storage to Shields
        Rect(720f, 640f, 840f, 700f),
        // Storage to Electrical
        Rect(440f, 580f, 480f, 640f),
        // Admin to Cafeteria Hallway
        Rect(740f, 360f, 800f, 410f),
        // Weapons to O2 and Navigation
        Rect(930f, 290f, 980f, 400f),
        Rect(920f, 440f, 980f, 500f),
        // Navigation to Shields
        Rect(980f, 520f, 1040f, 600f)
    )

    val vents = listOf(
        ShipVent("vent_cafe", "Cafeteria", Offset(730f, 160f), listOf("vent_admin", "vent_nav")),
        ShipVent("vent_admin", "Admin", Offset(830f, 520f), listOf("vent_cafe", "vent_nav")),
        ShipVent("vent_nav", "Navegação", Offset(1140f, 400f), listOf("vent_cafe", "vent_admin")),
        ShipVent("vent_weapons", "Armaria", Offset(1020f, 160f), listOf("vent_shields")),
        ShipVent("vent_shields", "Escudos", Offset(1000f, 700f), listOf("vent_weapons")),
        ShipVent("vent_elect", "Elétrica", Offset(350f, 470f), listOf("vent_sec", "vent_med")),
        ShipVent("vent_sec", "Segurança", Offset(250f, 430f), listOf("vent_elect", "vent_med")),
        ShipVent("vent_med", "Enfermaria", Offset(400f, 290f), listOf("vent_elect", "vent_sec")),
        ShipVent("vent_reac_top", "Reator Top", Offset(70f, 390f), listOf("vent_upper_eng")),
        ShipVent("vent_upper_eng", "Motor Superior", Offset(170f, 170f), listOf("vent_reac_top")),
        ShipVent("vent_reac_bot", "Reator Bot", Offset(70f, 530f), listOf("vent_lower_eng")),
        ShipVent("vent_lower_eng", "Motor Inferior", Offset(170f, 750f), listOf("vent_reac_bot"))
    )

    val initialTasks = listOf(
        ShipTask("t_wires_elec", "Fiação Elétrica", "Elétrica", Offset(380f, 460f), TaskType.WIRES),
        ShipTask("t_card_admin", "Passar Cartão", "Admin", Offset(760f, 510f), TaskType.CARD_SWIPE),
        ShipTask("t_shields", "Ativar Escudos", "Escudos", Offset(940f, 680f), TaskType.SHIELDS),
        ShipTask("t_nav_chart", "Calibrar Rota", "Navegação", Offset(1100f, 450f), TaskType.NAVIGATION),
        ShipTask("t_wires_caf", "Fiação da Cafeteria", "Cafeteria", Offset(500f, 150f), TaskType.WIRES),
        ShipTask("t_wires_sec", "Fiação de Segurança", "Segurança", Offset(300f, 490f), TaskType.WIRES),
        ShipTask("t_download_weapons", "Calibrar Miras", "Armaria", Offset(970f, 210f), TaskType.DOWNLOAD)
    )

    fun getRoomAt(pos: Offset): ClassicRoom? {
        return rooms.firstOrNull { it.bounds.contains(pos) }
    }

    fun isWalkable(pos: Offset): Boolean {
        // Check if point is inside any room bounds or corridor bounds
        if (rooms.any { it.bounds.contains(pos) }) return true
        if (corridors.any { it.contains(pos) }) return true
        return false
    }

}
