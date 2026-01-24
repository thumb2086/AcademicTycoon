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
import coil.compose.AsyncImage
import com.tycoon.academic.data.local.model.Question
import com.tycoon.academic.ui.viewmodel.FinanceViewModel
import com.tycoon.academic.ui.viewmodel.MiningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiningScreen(
    miningViewModel: MiningViewModel = hiltViewModel(),
    financeViewModel: FinanceViewModel = hiltViewModel()
) {
    // 使用 collectAsState 獲取題目列表
    val questions by miningViewModel.questions.collectAsState()
    var showDialog by remember { mutableStateOf<Question?>(null) }

    // 將路徑改為直接指向 assets 內的檔名，Repository 已處理識別邏輯
    val subjects = mapOf(
        "機械原理 (Mechanical)" to "mechanical.json",
        "高中數學 (High School)" to "highschool.json"
    )
    
    var selectedSubjectLabel by remember { mutableStateOf(subjects.keys.first()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // 當選擇變更時，載入題目
    LaunchedEffect(selectedSubjectLabel) {
        subjects[selectedSubjectLabel]?.let { fileName ->
            miningViewModel.loadQuestionsFromUrl(fileName)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("知識採礦場", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        // 考科選擇下拉選單
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedSubjectLabel,
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
                subjects.keys.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedSubjectLabel = label
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 題目列表
        if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
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
    }

    // 解析對話框
    showDialog?.let { question ->
        AlertDialog(
            onDismissRequest = { showDialog = null },
            title = { Text(if (showDialog?.q?.contains("?") == true) "解析說明" else "回答結果") },
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!question.image_url.isNullOrBlank() && question.image_url.startsWith("http")) {
                AsyncImage(
                    model = question.image_url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Text(text = question.q, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            
            question.options.forEachIndexed { index, option ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOptionSelected(index == question.a) },
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${index + 1}. $option",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "獎勵: $${question.reward}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
