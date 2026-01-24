package com.tycoon.academic.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.tycoon.academic.ui.FinanceViewModel

@Composable
fun AchievementsScreen(financeViewModel: FinanceViewModel = hiltViewModel()) {
    // 使用 collectAsState 監聽資料變化
    val userProfile by financeViewModel.userProfile.collectAsState(initial = null)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "學術成就",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        val profile = userProfile
        if (profile != null) {
            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("頭銜: ${profile.rank}")
                    // 修正欄位名稱為 correct_answers_count
                    Text("答對數: ${profile.correct_answers_count}")
                    Text("總答題數: ${profile.total_questions_answered}")
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }
}