package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AmongKidsColorScheme =
  darkColorScheme(
    primary = CyanAccent,
    onPrimary = SpaceDark,
    secondary = SoftPurple,
    onSecondary = Color.White,
    tertiary = AmberGold,
    onTertiary = SpaceDark,
    background = SpaceDark,
    onBackground = TextPrimary,
    surface = SpaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SpaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SpaceBorder
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = AmongKidsColorScheme,
    typography = Typography,
    content = content
  )
}

