package com.example.academictycoon.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.academictycoon.data.local.model.Question
import com.example.academictycoon.ui.FinanceViewModel
import com.example.academictycoon.ui.MiningViewModel

@Composable
fun MiningScreen(
    miningViewModel: MiningViewModel = hiltViewModel(),
    financeViewModel: FinanceViewModel = hiltViewModel()
) {
    val questions by miningViewModel.questions.collectAsState()
    var showDialog by remember { mutableStateOf<Question?>(null) }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(questions) { question ->
            QuestionCard(question = question) {
                if (it == question.a) {
                    financeViewModel.processReward(question.reward)
                }
                showDialog = question
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    showDialog?.let {
        AlertDialog(
            onDismissRequest = { showDialog = null },
            title = { Text("Explanation") },
            text = { Text(it.explanation) },
            confirmButton = {
                Button(onClick = { showDialog = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun QuestionCard(question: Question, onOptionSelected: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (question.image_url.isNotBlank()) {
                AsyncImage(
                    model = question.image_url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(text = question.q, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            question.options.forEachIndexed { index, option ->
                Text(
                    text = "${index + 1}. $option",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(index) }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}
