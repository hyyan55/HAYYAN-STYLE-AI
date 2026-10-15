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

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianBlack,
    primaryContainer = CharcoalElevated,
    onPrimaryContainer = GoldLight,
    secondary = ChampagneBeige,
    onSecondary = ObsidianBlack,
    secondaryContainer = NavyAccent,
    onSecondaryContainer = Color.White,
    tertiary = WarmTaupe,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = TextPrimaryDark,
    surface = CharcoalSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = CharcoalElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = CharcoalBorder,
    outlineVariant = CharcoalElevated,
    error = CrimsonError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = GoldVariant,
    onPrimary = Color.White,
    primaryContainer = GoldSubtle,
    onPrimaryContainer = ObsidianBlack,
    secondary = WarmTaupe,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceElevated,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = NavyAccent,
    onTertiary = Color.White,
    background = WarmWhiteBackground,
    onBackground = TextPrimaryLight,
    surface = WhiteSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    outlineVariant = LightBorder,
    error = CrimsonError,
    onError = Color.White
)

@Composable
fun HayyanStyleTheme(
    darkTheme: Boolean = true, // Default to sleek dark luxury menswear theme
    dynamicColor: Boolean = false, // Keep tailored luxury palette consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
