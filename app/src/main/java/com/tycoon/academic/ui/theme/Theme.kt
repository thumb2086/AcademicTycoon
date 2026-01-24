package com.tycoon.academic.ui.theme // 必須跟 MainActivity 的 Import 對上

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
private val DarkColorScheme = darkColorScheme(
    primary = FluorescentGreen,
    onPrimary = IndustrialBlack,
    secondary = FluorescentGreen,
    onSecondary = IndustrialBlack,
    background = IndustrialBlack,
    onBackground = FluorescentGreen,
    surface = IndustrialBlack,
    onSurface = FluorescentGreen
)

@Composable
fun AcademicTycoonTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
