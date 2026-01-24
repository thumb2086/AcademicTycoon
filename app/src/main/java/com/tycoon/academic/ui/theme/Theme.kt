package com.tycoon.academic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FluorescentGreen,
    onPrimary = IndustrialBlack,
    primaryContainer = IndustrialBlack,
    onPrimaryContainer = FluorescentGreen,
    secondary = FluorescentGreen,
    onSecondary = IndustrialBlack,
    background = IndustrialBlack,
    onBackground = FluorescentGreen,
    surface = IndustrialBlack,
    onSurface = FluorescentGreen,
    surfaceVariant = Color(0xFF1E1E1E), // 深灰色，用於卡片背景
    onSurfaceVariant = FluorescentGreen,
    outline = FluorescentGreen // 邊框使用螢光綠
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
