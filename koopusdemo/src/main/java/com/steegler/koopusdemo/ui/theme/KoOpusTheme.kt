package com.steegler.koopusdemo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00A6ED),
    secondary = Color(0xFF006494),
    tertiary = Color(0xFF003554)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00A6ED),
    secondary = Color(0xFF006494),
    tertiary = Color(0xFF003554)
)

@Composable
fun KoOpusTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
