package com.example.frem.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.frem.app.ui.theme.FremTypography

private val FremDarkColorScheme = darkColorScheme(
    primary = FremAccentLight,
    onPrimary = FremBlack,

    secondary = FremAccent,
    onSecondary = FremWhite,

    background = FremBlack,
    onBackground = FremTextPrimary,

    surface = FremDark,
    onSurface = FremTextPrimary,

    surfaceVariant = FremSurface,
    onSurfaceVariant = FremTextSecondary
)

private val FremLightColorScheme = lightColorScheme(
    primary = FremAccent,
    onPrimary = FremWhite,

    secondary = FremAccent,
    onSecondary = FremWhite,

    background = FremBackgroundLight,
    onBackground = FremTextPrimaryLight,

    surface = FremSurfaceLight,
    onSurface = FremTextPrimaryLight,

    surfaceVariant = Color(0xFFE9E9EC),
    onSurfaceVariant = FremTextSecondaryLight
)

@Composable
fun FremTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = when {

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> FremDarkColorScheme

        else -> FremLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FremTypography,
        content = content
    )
}