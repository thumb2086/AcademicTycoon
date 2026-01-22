package com.tycoon.academic.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// 基礎類別匯入
import com.tycoon.academic.ui.navigation.Screen
import com.tycoon.academic.ui.AuthViewModel
import com.tycoon.academic.ui.LoginScreen

// 畫面匯入
import com.tycoon.academic.ui.screens.MiningScreen
import com.tycoon.academic.ui.screens.AchievementsScreen
import com.tycoon.academic.ui.screens.BlackMarketScreen

// 賭場相關畫面 (確保路徑正確)
import com.tycoon.academic.ui.screens.casino.CasinoScreen
import com.tycoon.academic.ui.screens.casino.RouletteScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser = authViewModel.getCurrentUser()
    val startDestination = if (currentUser != null) Screen.Mining.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Mining.route) {
                    popUpTo(Screen.Login.route) {
                        this.inclusive = true
                    }
                }
            })
        }
        composable(Screen.Mining.route) {
            MiningScreen()
        }
        composable(Screen.Casino.route) {
            // 確保 CasinoScreen 接受 navController 參數
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