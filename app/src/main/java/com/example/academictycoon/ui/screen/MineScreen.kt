package com.example.academictycoon.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.academictycoon.ui.viewmodel.FinanceViewModel
import com.example.academictycoon.ui.viewmodel.MineViewModel

@Composable
fun MineScreen(
    mineViewModel: MineViewModel = hiltViewModel(),
    financeViewModel: FinanceViewModel = hiltViewModel()
) {
    val questions by mineViewModel.questions.collectAsStateWithLifecycle()
    val currentQuestionIndex by mineViewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    
    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }

    if (questions.isNotEmpty() && currentQuestionIndex < questions.size) {
        val currentQuestion = questions[currentQuestionIndex]

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = currentQuestion.q, style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        if (currentQuestion.image_url.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(currentQuestion.image_url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Question Image",
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        currentQuestion.options.forEachIndexed { index, option ->
                            Button(
                                onClick = {
                                    if (index == currentQuestion.a) {
                                        financeViewModel.addReward(currentQuestion.reward.toLong())
                                        dialogText = "Correct!\n\n${currentQuestion.explanation}"
                                    } else {
                                        dialogText = "Incorrect. The correct answer was: ${currentQuestion.options[currentQuestion.a]}"
                                    }
                                    showDialog = true
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Text(text = option)
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    mineViewModel.nextQuestion()
                },
                title = { Text(if (dialogText.startsWith("Correct")) "Result" else "Incorrect") },
                text = { Text(dialogText) },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            mineViewModel.nextQuestion()
                        }
                    ) {
                        Text("Next Question")
                    }
                }
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
