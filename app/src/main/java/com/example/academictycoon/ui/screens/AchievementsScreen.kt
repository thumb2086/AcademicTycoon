package com.example.academictycoon.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.academictycoon.ui.FinanceViewModel

@Composable
fun AchievementsScreen(financeViewModel: FinanceViewModel = hiltViewModel()) {
    val userProfile by financeViewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        userProfile?.let {
            Text("Your Rank", style = MaterialTheme.typography.headlineMedium)
            Text(it.rank, style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(bottom = 32.dp))
            
            Text("Correct Answers", style = MaterialTheme.typography.headlineMedium)
            Text(it.correct_count.toString(), style = MaterialTheme.typography.displaySmall)
        }
    }
}
