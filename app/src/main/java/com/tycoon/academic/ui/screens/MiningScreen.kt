package com.tycoon.academic.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.tycoon.academic.data.local.model.Question
import com.tycoon.academic.ui.viewmodel.FinanceViewModel
import com.tycoon.academic.ui.viewmodel.MiningViewModel

data class AnswerResult(
    val isCorrect: Boolean,
    val question: Question,
    val rewardAmount: Long = 0,
    val payDebtAmount: Long = 0,
    val pocketAmount: Long = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiningScreen(
    miningViewModel: MiningViewModel = hiltViewModel(),
    financeViewModel: FinanceViewModel = hiltViewModel()
) {
    val subjects by miningViewModel.subjects.collectAsState()
    val selectedSubject by miningViewModel.selectedSubject.collectAsState()
    val filteredQuestions by miningViewModel.filteredQuestions.collectAsState()
    val currentIndex by miningViewModel.currentIndex.collectAsState()
    val userProfile by financeViewModel.userProfile.collectAsState()
    
    val currentQuestion = filteredQuestions.getOrNull(currentIndex)
    
    var answerFeedback by remember { mutableStateOf<AnswerResult?>(null) }
    var isFilterExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // 頂部狀態列
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("知識採礦場", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Text("目前的題庫範圍", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 優化：更明顯的切換按鈕
                Surface(
                    onClick = { isFilterExpanded = true },
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedSubject ?: "全部題庫",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp).padding(start = 4.dp))
                    }
                    
                    DropdownMenu(expanded = isFilterExpanded, onDismissRequest = { isFilterExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("全部題庫") },
                            onClick = { miningViewModel.filterBySubject(null); isFilterExpanded = false }
                        )
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject) },
                                onClick = { miningViewModel.filterBySubject(subject); isFilterExpanded = false }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))

                // 錢包資訊
                Column(horizontalAlignment = Alignment.End) {
                    Text("餘額: $${userProfile?.balance ?: 0}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if ((userProfile?.debt ?: 0) > 0) {
                        Text("債務: $${userProfile?.debt}", color = Color.Red, fontSize = 10.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        // 進度顯示
        if (filteredQuestions.isNotEmpty()) {
            val progress = (currentIndex + 1).toFloat() / filteredQuestions.size
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            Text(
                "進度: ${currentIndex + 1} / ${filteredQuestions.size}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (currentQuestion == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("正在載入題目...", color = Color.Gray)
                }
            }
        } else {
            ChallengeCard(question = currentQuestion) { isCorrect ->
                val hasDebt = (userProfile?.debt ?: 0) > 0
                val reward = currentQuestion.reward.toLong()
                
                if (isCorrect) {
                    financeViewModel.addReward(reward, fromQuestion = true)
                    answerFeedback = AnswerResult(
                        isCorrect = true,
                        question = currentQuestion,
                        rewardAmount = reward,
                        payDebtAmount = if (hasDebt) (reward * 0.8).toLong() else 0,
                        pocketAmount = if (hasDebt) (reward * 0.2).toLong() else reward
                    )
                } else {
                    financeViewModel.recordWrongAnswer()
                    answerFeedback = AnswerResult(isCorrect = false, question = currentQuestion)
                }
            }
        }
    }

    // 答題反饋對話框
    answerFeedback?.let { result ->
        AlertDialog(
            onDismissRequest = { 
                miningViewModel.nextQuestion()
                answerFeedback = null 
            },
            containerColor = Color(0xFF1E1E1E),
            title = {
                Text(
                    text = if (result.isCorrect) "答對了！ 🎉" else "答錯了 😢",
                    color = if (result.isCorrect) Color(0xFF4CAF50) else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (result.isCorrect) {
                        Text("獲得獎勵：$${result.rewardAmount}", fontWeight = FontWeight.Bold, color = Color.White)
                        if (result.payDebtAmount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("依據 80/20 負債條款：", fontSize = 12.sp, color = Color.Gray)
                            Text("• 自動償還債務：-$${result.payDebtAmount}", color = Color.Red, fontSize = 12.sp)
                            Text("• 實領研究經費：+$${result.pocketAmount}", color = Color(0xFF4CAF50), fontSize = 12.sp)
                        }
                    } else {
                        val correctOption = result.question.options.getOrNull(result.question.a) ?: "未知"
                        Text("正確答案是：", color = Color.Gray, fontSize = 12.sp)
                        Text(correctOption, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("【解析說明】", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(result.question.explanation, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        miningViewModel.nextQuestion()
                        answerFeedback = null 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("下一題", color = Color.Black)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp).padding(start = 4.dp), tint = Color.Black)
                }
            }
        )
    }
}

@Composable
fun ChallengeCard(question: Question, onOptionSelected: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.95f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()) // 加入捲動，避免題目太長
        ) {
            // 科目標籤
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = question.subject,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (!question.image_url.isNullOrBlank() && question.image_url.startsWith("http")) {
                AsyncImage(
                    model = question.image_url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 優化：降低題目字體大小，避免佔空間
            Text(
                text = question.q, 
                style = MaterialTheme.typography.titleMedium, 
                lineHeight = 24.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            question.options.forEachIndexed { index, option ->
                OutlinedButton(
                    onClick = { onOptionSelected(index == question.a) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "${index + 1}. $option",
                        modifier = Modifier.padding(6.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
