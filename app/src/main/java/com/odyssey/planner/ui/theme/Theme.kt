package com.odyssey.planner.ui.theme

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

private val LightColors = lightColorScheme(
    primary = AegeanBlue,
    onPrimary = MarbleWhite,
    primaryContainer = AegeanBlueLight,
    onPrimaryContainer = MarbleWhite,
    secondary = Terracotta,
    onSecondary = MarbleWhite,
    secondaryContainer = TerracottaLight,
    onSecondaryContainer = MarbleWhite,
    tertiary = OlympianGold,
    onTertiary = InkBrown,
    background = Parchment,
    onBackground = InkBrown,
    surface = MarbleWhite,
    onSurface = InkBrown,
    surfaceVariant = ParchmentDark,
    onSurfaceVariant = StoneGray,
    outline = StoneGray
)

private val DarkColors = darkColorScheme(
    primary = AegeanBlueLight,
    onPrimary = NightParchment,
    primaryContainer = AegeanBlue,
    onPrimaryContainer = NightParchment,
    secondary = TerracottaLight,
    onSecondary = NightParchment,
    tertiary = OlympianGoldLight,
    onTertiary = AegeanBlueDark,
    background = NightAegean,
    onBackground = NightParchment,
    surface = NightSurface,
    onSurface = NightParchment,
    surfaceVariant = AegeanBlueDark,
    onSurfaceVariant = ParchmentDark,
    outline = StoneGray
)

@Composable
fun OdysseyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OdysseyTypography,
        content = content
    )
}
