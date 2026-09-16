package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ShipTask
import com.example.model.TaskType
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PlayerBlue
import com.example.ui.theme.PlayerPink
import com.example.ui.theme.PlayerRed
import com.example.ui.theme.PlayerYellow
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TaskMinigameDialog(
    task: ShipTask,
    onCompleteTask: (ShipTask) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SpaceDark),
            border = androidx.compose.foundation.BorderStroke(2.dp, AmberGold)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚡ ${task.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = BrightYellow
                        )
                        Text(
                            text = "Local: ${task.room}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (task.type) {
                    TaskType.WIRES -> {
                        WiresMinigame(onFinish = {
                            onCompleteTask(task)
                            onDismiss()
                        })
                    }
                    TaskType.CARD_SWIPE -> {
                        CardSwipeMinigame(onFinish = {
                            onCompleteTask(task)
                            onDismiss()
                        })
                    }
                    TaskType.SHIELDS -> {
                        ShieldsMinigame(onFinish = {
                            onCompleteTask(task)
                            onDismiss()
                        })
                    }
                    else -> {
                        DefaultCalibrationMinigame(onFinish = {
                            onCompleteTask(task)
                            onDismiss()
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun WiresMinigame(onFinish: () -> Unit) {
    val wireColors = remember { listOf(PlayerRed, PlayerBlue, PlayerYellow, PlayerPink) }
    val connected = remember { mutableStateListOf(false, false, false, false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131C2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Toque nos conectores para ligar os cabos!",
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))

        wireColors.forEachIndexed { index, color ->
            val isConn = connected[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left wire end
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .clickable {
                            connected[index] = !connected[index]
                            if (connected.all { it }) onFinish()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔌", fontSize = 16.sp)
                }

                // Middle wire line
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (isConn) 6.dp else 2.dp)
                        .padding(horizontal = 8.dp)
                        .background(if (isConn) color else Color.Gray.copy(alpha = 0.3f))
                )

                // Right wire slot
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isConn) color else Color(0xFF1E293B))
                        .border(1.dp, color, RoundedCornerShape(8.dp))
                        .clickable {
                            connected[index] = true
                            if (connected.all { it }) onFinish()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isConn) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = SpaceDark, modifier = Modifier.size(20.dp))
                    } else {
                        Text("⚡", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CardSwipeMinigame(onFinish: () -> Unit) {
    var swipeCount by remember { mutableStateOf(0) }
    var isSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131C2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Passe o Cartão de Acesso do Tripulante!",
            fontSize = 12.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Card Graphic
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(90.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1E3A5F))
                .border(2.dp, CyanAccent, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("AMONG KIDS ID", fontWeight = FontWeight.Black, color = CyanAccent, fontSize = 12.sp)
                    Text("💳", fontSize = 18.sp)
                }
                Text("Tripulante Oficial", fontSize = 11.sp, color = Color.White)
                Text("CODE: 9842-SKY", fontSize = 9.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                swipeCount++
                if (swipeCount >= 2) {
                    isSuccess = true
                    onFinish()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (isSuccess) NeonGreen else CyanAccent),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.85f).height(44.dp)
        ) {
            Text(
                text = if (isSuccess) "AUTORIZADO! ✅" else "PASSAR CARTÃO NO LEITOR 💳",
                color = SpaceDark,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ShieldsMinigame(onFinish: () -> Unit) {
    val activeShields = remember { mutableStateListOf(false, false, false, false, false, false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131C2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Toque nas células de energia para ativar os escudos!",
            fontSize = 12.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (col in 0..2) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (row in 0..1) {
                        val idx = col * 2 + row
                        val isOn = activeShields[idx]
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isOn) CyanAccent else Color(0xFF4A1525))
                                .border(2.dp, if (isOn) NeonGreen else CoralRed, CircleShape)
                                .clickable {
                                    activeShields[idx] = !activeShields[idx]
                                    if (activeShields.all { it }) onFinish()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isOn) "🛡️" else "🔴", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultCalibrationMinigame(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131C2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Calibrando sistemas da nave...", fontSize = 13.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Concluir Calibração 🚀", color = SpaceDark, fontWeight = FontWeight.Bold)
        }
    }
}
