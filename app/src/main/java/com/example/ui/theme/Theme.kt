package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AlAwlaGoldPrimary,
    onPrimary = Color(0xFF281E00),
    primaryContainer = AlAwlaEmeraldLight,
    onPrimaryContainer = Color(0xFFE8F6F0),
    secondary = AlAwlaGoldLight,
    onSecondary = Color(0xFF2B2000),
    secondaryContainer = AlAwlaEmeraldDark,
    onSecondaryContainer = Color(0xFFD0F0E4),
    tertiary = AlAwlaBronze,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = AlAwlaEmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = AlAwlaEmeraldContainer,
    onPrimaryContainer = AlAwlaOnEmeraldContainer,
    secondary = AlAwlaGoldDark,
    onSecondary = Color.White,
    secondaryContainer = AlAwlaGoldContainer,
    onSecondaryContainer = AlAwlaOnGoldContainer,
    tertiary = AlAwlaBronze,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
