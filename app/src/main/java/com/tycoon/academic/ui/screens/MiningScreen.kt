package com.tycoon.academic.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // 修正：LazyColumn 的 items 擴充函數
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue // 修正：解決 'getValue' 報錯的關鍵
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.tycoon.academic.data.local.model.Question
// 修正：指向新的 ViewModel 路徑
import com.tycoon.academic.ui.viewmodel.FinanceViewModel
import com.tycoon.academic.ui.viewmodel.MiningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiningScreen(
    miningViewModel: MiningViewModel = hiltViewModel(),
    financeViewModel: FinanceViewModel = hiltViewModel()
) {
    // 修正：使用 collectAsState 並提供初始值
    // 因為有了 import getValue，這裡的 by 語法現在會正常運作，questions 會被識別為 List<Question>
    val questions by miningViewModel.questions.collectAsState(initial = emptyList())
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
                label = { Text("選擇考科") },
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
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // 因為上面修好了 questions 的型別，這裡的 items 就不會再報 Type mismatch
            items(questions) { question ->
                QuestionCard(question = question) { isCorrect ->
                    if (isCorrect) {
                        financeViewModel.addReward(question.reward.toLong())
                    }
                    showDialog = question
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    showDialog?.let { question ->
        AlertDialog(
            onDismissRequest = { showDialog = null },
            title = { Text("解析說明") },
            text = { Text(question.explanation) },
            confirmButton = {
                Button(onClick = { showDialog = null }) {
                    Text("確定")
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