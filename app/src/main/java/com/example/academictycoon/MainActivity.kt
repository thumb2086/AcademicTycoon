package com.example.academictycoon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.academictycoon.ui.MainScreen
import com.example.academictycoon.ui.theme.AcademicTycoonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AcademicTycoonTheme {
                MainScreen()
            }
        }
    }
}
