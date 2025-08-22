package com.angeluz.freyja.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val DarkColors = darkColorScheme(
    primary = RuneBlue,
    surface = RuneBlack,
    background = RuneBlack,
    onPrimary = RuneText,
    onSurface = RuneText,
    onBackground = RuneText,
)

@Composable
fun FreyjaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, typography = Typography, content = content)
}
