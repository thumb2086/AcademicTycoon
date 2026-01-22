package com.tycoon.academic.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tycoon.academic.ui.AuthViewModel
import com.tycoon.academic.ui.LoginScreen
import com.example.academictycoon.ui.screens.AchievementsScreen
import com.example.academictycoon.ui.screens.BlackMarketScreen
import com.example.academictycoon.ui.screens.MiningScreen
import com.example.academictycoon.ui.screens.casino.CasinoScreen
import com.example.academictycoon.ui.screens.casino.RouletteScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier, authViewModel: AuthViewModel = hiltViewModel()) {
    val currentUser = authViewModel.getCurrentUser()
    val startDestination = if (currentUser != null) Screen.Mining.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Mining.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Mining.route) {
            MiningScreen()
        }
        composable(Screen.Casino.route) {
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
