package com.example.academictycoon.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.academictycoon.ui.navigation.AppNavigation
import com.example.academictycoon.ui.navigation.Screen
import com.example.academictycoon.ui.theme.DebtRed
import com.example.academictycoon.ui.theme.FluorescentGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: FinanceViewModel = hiltViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    val navController = rememberNavController()
    val screens = listOf(Screen.Mining, Screen.Casino, Screen.BlackMarket, Screen.Achievements)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Academic Tycoon") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = FluorescentGreen
                ),
                actions = {
                    userProfile?.let { profile ->
                        Row(modifier = Modifier.padding(end = 16.dp)) {
                            Text(text = "Balance: ${profile.balance}", color = FluorescentGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            if (profile.debt > 0) {
                                val infiniteTransition = rememberInfiniteTransition(label = "​")
                                val alpha by infiniteTransition.animateFloat(
                                    initialValue = 0.5f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(500),
                                        repeatMode = RepeatMode.Reverse
                                    ), label = "​"
                                )
                                Text(
                                    text = "Debt: ${profile.debt}",
                                    color = DebtRed,
                                    modifier = Modifier.graphicsLayer { this.alpha = alpha }
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.route == screen.route,
                        onClick = { 
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
