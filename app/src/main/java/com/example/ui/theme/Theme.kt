package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
  lightColorScheme(
    primary = IndigoPrimary,
    secondary = PurpleAccent,
    tertiary = AmberAccent,
    background = FrostedBackground,
    surface = FrostedCardBgSolid,
    surfaceVariant = FrostedCardBg,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = Slate900,
    onSurface = Slate800,
    outline = FrostedBorderOutline,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = IndigoPrimary,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = PurpleAccent,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = AmberAccent
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = IndigoPrimaryLight,
    secondary = PurpleAccent,
    tertiary = AmberAccent,
    background = Slate900,
    surface = Slate800,
    surfaceVariant = Color(0xFF334155),
    onPrimary = Slate900,
    onSecondary = Slate900,
    onBackground = FrostedBackground,
    onSurface = FrostedBackground
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

