package com.tycoon.academic.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

// 基礎類別匯入
import com.tycoon.academic.ui.navigation.Screen
import com.tycoon.academic.ui.AuthViewModel
import com.tycoon.academic.ui.LoginScreen

// 畫面匯入
import com.tycoon.academic.ui.screens.MiningScreen
import com.tycoon.academic.ui.screens.AchievementsScreen
import com.tycoon.academic.ui.screens.BlackMarketScreen

// 賭場相關畫面
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

    // 獲取當前路由狀態
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 定義哪些頁面需要顯示底部導航列
    val bottomBarScreens = listOf(
        Screen.Mining,
        Screen.Casino,
        Screen.BlackMarket,
        Screen.Achievements
    )

    Scaffold(
        bottomBar = {
            // 只有在主功能頁面才顯示導航列（排除登入頁、輪盤頁等）
            if (currentRoute in bottomBarScreens.map { it.route }) {
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { 
                                screen.icon?.let { Icon(it, contentDescription = screen.title) } 
                            },
                            label = { 
                                screen.title?.let { Text(it) } 
                            },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    // 避免在 BackStack 中累積重複頁面
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = startDestination, 
            modifier = modifier.padding(innerPadding)
        ) {
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
}