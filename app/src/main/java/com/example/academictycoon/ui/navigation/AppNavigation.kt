package com.example.academictycoon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.academictycoon.ui.screens.AchievementsScreen
import com.example.academictycoon.ui.screens.BlackMarketScreen
import com.example.academictycoon.ui.screens.MiningScreen
import com.example.academictycoon.ui.screens.casino.CasinoScreen
import com.example.academictycoon.ui.screens.casino.RouletteScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Screen.Mining.route, modifier = modifier) {
        composable(Screen.Mining.route) {
            MiningScreen()
        }
        composable(Screen.Casino.route) {
            // Pass NavController to allow navigation to sub-screens like Roulette
            CasinoScreen(navController = navController)
        }
        composable(Screen.BlackMarket.route) {
            BlackMarketScreen()
        }
        composable(Screen.Achievements.route) {
            AchievementsScreen()
        }
        composable(Screen.Roulette.route) {
            RouletteScreen()
        }
    }
}
