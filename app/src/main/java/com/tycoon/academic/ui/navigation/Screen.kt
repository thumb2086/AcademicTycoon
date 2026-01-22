package com.tycoon.academic.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    object Login : Screen("login")
    object Mining : Screen("mining", "研發礦場", Icons.Default.Build)
    object Casino : Screen("casino", "核心賭場", Icons.Default.MonetizationOn)
    object BlackMarket : Screen("black_market", "借貸黑市", Icons.Default.AccountBalance)
    object Achievements : Screen("achievements", "成就系統", Icons.Default.Star)
    object Roulette : Screen("roulette", "輪盤賭", Icons.Default.Casino) // Added Roulette Screen
}
