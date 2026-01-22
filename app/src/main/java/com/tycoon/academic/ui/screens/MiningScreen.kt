package com.tycoon.academic.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tycoon.academic.data.local.model.Question
import com.tycoon.academic.ui.FinanceViewModel
import com.tycoon.academic.ui.MiningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiningScreen(
    miningViewModel: MiningViewModel = hiltViewModel(),
    financeViewModel: FinanceViewModel = hiltViewModel()
) {
    val questions by miningViewModel.questions.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf<Question?>(null) }

    // Subject Selection
    val subjects = mapOf(
        "Mechanical" to "https://raw.githubusercontent.com/thumb2086/AcademicTycoon/main/app/src/main/assets/mechanical.json",
        "High School" to "https://raw.githubusercontent.com/thumb2086/AcademicTycoon/main/app/src/main/assets/highschool.json"
    )
    var selectedSubject by remember { mutableStateOf("Mechanical") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Load questions when the selected subject changes
    LaunchedEffect(selectedSubject) {
        subjects[selectedSubject]?.let { url ->
            miningViewModel.loadQuestionsFromUrl(url)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Dropdown Menu
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedSubject,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Subject") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                subjects.keys.forEach { subjectName ->
                    DropdownMenuItem(
                        text = { Text(subjectName) },
                        onClick = {
                            selectedSubject = subjectName
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Questions List
        LazyColumn {
            items(questions) { question ->
                QuestionCard(question = question) { isCorrect ->
                    if (isCorrect) {
                        financeViewModel.processReward(question.reward)
                    }
                    showDialog = question
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
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
fun QuestionCard(question: Question, onOptionSelected: (Boolean) -> Unit) {
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
                        .clickable { onOptionSelected(index == question.a) }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}
