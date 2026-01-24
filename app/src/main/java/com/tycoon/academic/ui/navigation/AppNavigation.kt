package com.tycoon.academic.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import com.tycoon.academic.ui.viewmodel.FinanceViewModel

// 賭場相關畫面
import com.tycoon.academic.ui.screens.casino.CasinoScreen
import com.tycoon.academic.ui.screens.casino.RouletteScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    financeViewModel: FinanceViewModel = hiltViewModel()
) {
    val currentUser = authViewModel.getCurrentUser()
    val startDestination = if (currentUser != null) Screen.Mining.route else Screen.Login.route

    // 獲取當前路由狀態
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val userProfile by financeViewModel.userProfile.collectAsState()

    // 定義哪些頁面需要顯示底部導航列
    val bottomBarScreens = listOf(
        Screen.Mining,
        Screen.Casino,
        Screen.BlackMarket,
        Screen.Achievements
    )

    Scaffold(
        topBar = {
            // 固定頂部狀態欄：顯示金額與債務，只在登入後顯示
            if (currentRoute != Screen.Login.route) {
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 2.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "可用資金: $${userProfile?.balance ?: 0}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if ((userProfile?.debt ?: 0L) > 0) {
                                Text(
                                    "剩餘債務: $${userProfile?.debt}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red
                                )
                            }
                        }
                        
                        // 顯示頭銜
                        Text(
                            userProfile?.rank ?: "學術難民",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                    }
                }
            }
        },
        bottomBar = {
            // 只有在主功能頁面才顯示導航列
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
