package com.booknest.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkAmberPrimary,
    onPrimary = DarkAmberOnPrimary,
    primaryContainer = DarkAmberPrimaryContainer,
    onPrimaryContainer = DarkAmberOnPrimaryContainer,
    secondary = DarkIndigoSecondary,
    onSecondary = DarkIndigoOnSecondary,
    secondaryContainer = DarkIndigoSecondaryContainer,
    onSecondaryContainer = DarkIndigoOnSecondaryContainer,
    tertiary = SageTertiaryContainer,
    onTertiary = SageOnTertiaryContainer,
    tertiaryContainer = SageTertiary,
    onTertiaryContainer = SageTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = WarmAmberPrimary,
    onPrimary = WarmAmberOnPrimary,
    primaryContainer = WarmAmberPrimaryContainer,
    onPrimaryContainer = WarmAmberOnPrimaryContainer,
    secondary = DeepIndigoSecondary,
    onSecondary = DeepIndigoOnSecondary,
    secondaryContainer = DeepIndigoSecondaryContainer,
    onSecondaryContainer = DeepIndigoOnSecondaryContainer,
    tertiary = SageTertiary,
    onTertiary = SageOnTertiary,
    tertiaryContainer = SageTertiaryContainer,
    onTertiaryContainer = SageOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

@Composable
fun BookNestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surface.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
