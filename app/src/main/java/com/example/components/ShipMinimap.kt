package com.example.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ClassicTheSkeldMap
import com.example.model.ShipTask
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ShipMinimapDialog(
    playerPosition: Offset,
    tasks: List<ShipTask>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SpaceDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanAccent)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "🗺️ MAPA DA NAVE ESPACIAL",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = CyanAccent
                        )
                        Text(
                            text = "The Skeld - Mapa Clássico",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF090E17))
                        .border(1.dp, SpaceBorder, RoundedCornerShape(14.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                        val w = size.width
                        val h = size.height

                        // Map coords: 1200 x 900
                        val scaleX = w / ClassicTheSkeldMap.MAP_WIDTH
                        val scaleY = h / ClassicTheSkeldMap.MAP_HEIGHT

                        fun mapX(x: Float) = x * scaleX
                        fun mapY(y: Float) = y * scaleY

                        // Draw Corridors
                        ClassicTheSkeldMap.corridors.forEach { c ->
                            drawRoundRect(
                                color = Color(0xFF1E293B),
                                topLeft = Offset(mapX(c.left), mapY(c.top)),
                                size = Size(mapX(c.width), mapY(c.height)),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                            )
                        }

                        // Draw Rooms
                        ClassicTheSkeldMap.rooms.forEach { r ->
                            val rx = mapX(r.bounds.left)
                            val ry = mapY(r.bounds.top)
                            val rw = mapX(r.bounds.width)
                            val rh = mapY(r.bounds.height)

                            drawRoundRect(
                                color = Color(0xFF1E3A5F),
                                topLeft = Offset(rx, ry),
                                size = Size(rw, rh),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                            drawRoundRect(
                                color = Color(0xFF3B82F6),
                                topLeft = Offset(rx, ry),
                                size = Size(rw, rh),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                                style = Stroke(width = 1.dp.toPx())
                            )

                            // Room name label
                            drawContext.canvas.nativeCanvas.apply {
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.WHITE
                                    textSize = 8.sp.toPx()
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                                drawText(r.name, rx + rw / 2f, ry + rh / 2f + 3.dp.toPx(), paint)
                            }
                        }

                        // Draw Tasks (! markers)
                        tasks.filter { !it.isCompleted }.forEach { t ->
                            val tx = mapX(t.position.x)
                            val ty = mapY(t.position.y)
                            drawCircle(color = AmberGold, radius = 4.dp.toPx(), center = Offset(tx, ty))
                        }

                        // Draw Emergency Table
                        val ex = mapX(ClassicTheSkeldMap.EMERGENCY_TABLE_POS.x)
                        val ey = mapY(ClassicTheSkeldMap.EMERGENCY_TABLE_POS.y)
                        drawCircle(color = CoralRed, radius = 5.dp.toPx(), center = Offset(ex, ey))

                        // Draw Player Blinking Dot
                        val px = mapX(playerPosition.x)
                        val py = mapY(playerPosition.y)
                        drawCircle(
                            color = NeonGreen,
                            radius = 6.dp.toPx(),
                            center = Offset(px, py)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(px, py)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem("🟢 Você", NeonGreen)
                    LegendItem("🔴 Botão Emergência", CoralRed)
                    LegendItem("🟡 Tarefas", AmberGold)
                }
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = TextPrimary)
    }
}
