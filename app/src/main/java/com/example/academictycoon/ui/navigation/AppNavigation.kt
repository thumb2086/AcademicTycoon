package com.example.academictycoon.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.academictycoon.ui.screens.AchievementsScreen
import com.example.academictycoon.ui.screens.BlackMarketScreen
import com.example.academictycoon.ui.screens.MiningScreen
import com.example.academictycoon.ui.screens.casino.CasinoScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Screen.Mining.route, modifier = modifier) {
        composable(Screen.Mining.route) {
            MiningScreen()
        }
        composable(Screen.Casino.route) {
            CasinoScreen()
        }
        composable(Screen.BlackMarket.route) {
            BlackMarketScreen()
        }
        composable(Screen.Achievements.route) {
            AchievementsScreen()
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
