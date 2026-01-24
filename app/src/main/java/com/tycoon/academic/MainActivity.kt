package com.tycoon.academic // 必須改成新的包名

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.tycoon.academic.ui.navigation.AppNavigation // 指向新路徑
import com.tycoon.academic.ui.theme.AcademicTycoonTheme // 指向新路徑
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AcademicTycoonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 使用你之前寫好的 AppNavigation
                    AppNavigation(navController = rememberNavController())
                }
            }
        }
    }
}