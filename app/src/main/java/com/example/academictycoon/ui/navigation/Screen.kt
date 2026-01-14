package com.example.academictycoon.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Mine : Screen("mine", "研發礦場", Icons.Default.Home)
    object Casino : Screen("casino", "核心賭場", Icons.Default.PlayArrow)
    object BlackMarket : Screen("market", "借貸黑市", Icons.Default.List)
    object Achievements : Screen("achievements", "成就系統", Icons.Default.Star)
}

val bottomNavItems = listOf(
    Screen.Mine,
    Screen.Casino,
    Screen.BlackMarket,
    Screen.Achievements
)
