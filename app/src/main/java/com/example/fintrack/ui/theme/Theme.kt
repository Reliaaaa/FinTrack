package com.example.fintrack.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FinTrackColorScheme = darkColorScheme(
    primary = PrimaryCobalt,
    onPrimary = DarkNavyBackground,
    primaryContainer = AccentBlue,
    onPrimaryContainer = TextForeground,
    secondary = SecondaryMint,
    onSecondary = SecondaryMintDark,
    background = DarkNavyBackground,
    onBackground = TextForeground,
    surface = DarkNavyCard,
    onSurface = TextForeground,
    surfaceVariant = DarkNavyMuted,
    onSurfaceVariant = TextMuted,
    outline = DarkNavyBorder,
    error = DangerRed
)

@Composable
fun FinTrackTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkNavyBackground.toArgb()
                window.navigationBarColor = DarkNavyBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = FinTrackColorScheme,
        typography = Typography,
        content = content
    )
}
