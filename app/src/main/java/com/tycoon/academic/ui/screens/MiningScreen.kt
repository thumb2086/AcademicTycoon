package com.tycoon.academic.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val questions by miningViewModel.questions.collectAsState()
    var showDialog by remember { mutableStateOf<Question?>(null) }

    val subjects = mapOf(
        "機械原理 (Mechanical)" to "mechanical.json",
        "高中數學 (High School)" to "highschool.json"
    )
    
    var selectedSubjectLabel by remember { mutableStateOf(subjects.keys.first()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedSubjectLabel) {
        subjects[selectedSubjectLabel]?.let { fileName ->
            miningViewModel.loadQuestionsFromUrl(fileName)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "知識採礦場", 
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 考科選擇下拉選單 - 調整顏色使其更符合主題
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedSubjectLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("選擇考科", color = MaterialTheme.colorScheme.primary) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                subjects.keys.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(label, color = MaterialTheme.colorScheme.primary) },
                        onClick = {
                            selectedSubjectLabel = label
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(questions) { question ->
                    QuestionCard(question = question) { isCorrect ->
                        if (isCorrect) {
                            // 修正：傳入 fromQuestion = true 以觸發頭銜計算
                            financeViewModel.addReward(question.reward.toLong(), fromQuestion = true)
                        } else {
                            // 修正：紀錄錯誤回答以更新分析數據
                            financeViewModel.recordWrongAnswer()
                        }
                        showDialog = question
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    showDialog?.let { question ->
        AlertDialog(
            onDismissRequest = { showDialog = null },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = MaterialTheme.colorScheme.primary,
            title = { Text(if (showDialog?.q?.contains("?") == true) "解析說明" else "回答結果") },
            text = { Text(question.explanation) },
            confirmButton = {
                TextButton(onClick = { showDialog = null }) {
                    Text("確定", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

@Composable
fun QuestionCard(question: Question, onOptionSelected: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
            
            Text(
                text = question.q, 
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            question.options.forEachIndexed { index, option ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOptionSelected(index == question.a) },
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${index + 1}. $option",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
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
