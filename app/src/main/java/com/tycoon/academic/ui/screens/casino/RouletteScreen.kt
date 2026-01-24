package com.tycoon.academic.ui.screens.casino

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tycoon.academic.ui.viewmodel.FinanceViewModel
import kotlin.random.Random

private val redNumbers = setOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouletteScreen(financeViewModel: FinanceViewModel = hiltViewModel()) {
    var betAmount by remember { mutableStateOf("") }
    var gameResult by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    val userProfile by financeViewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("輪盤賭", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(
            value = betAmount,
            onValueChange = { betAmount = it },
            label = { Text("下注金額") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        val currentBalance = userProfile?.balance ?: 0L
        val bet = betAmount.toLongOrNull() ?: 0L

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    playRoulette(betAmount, BetType.RED, financeViewModel) { result, message ->
                        gameResult = result
                        feedbackMessage = message
                    }
                },
                enabled = bet > 0 && bet <= currentBalance
            ) {
                Text("押紅色")
            }
            Button(
                onClick = {
                    playRoulette(betAmount, BetType.BLACK, financeViewModel) { result, message ->
                        gameResult = result
                        feedbackMessage = message
                    }
                },
                enabled = bet > 0 && bet <= currentBalance
            ) {
                Text("押黑色")
            }
        }

        if (bet > currentBalance && bet > 0) {
            Text("資金不足！", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        if (gameResult != null) {
            val (number, color) = gameResult!!
            val resultColor = if (color == "紅色") Color.Red else if (color == "黑色") Color.Black else Color.Green
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("開獎結果", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = number.toString(),
                    fontSize = 80.sp,
                    color = resultColor,
                    textAlign = TextAlign.Center
                )
                feedbackMessage?.let {
                    Text(it, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

enum class BetType { RED, BLACK }

private fun playRoulette(
    betAmountStr: String,
    betType: BetType,
    financeViewModel: FinanceViewModel,
    onResult: (Pair<Int, String>, String) -> Unit
) {
    val bet = betAmountStr.toLongOrNull()
    if (bet == null || bet <= 0) {
        onResult(Pair(0, "無效"), "請輸入有效的下注金額。")
        return
    }

    financeViewModel.deductBet(bet, "roulette")

    val winningNumber = Random.nextInt(0, 37) // 0-36
    val winningColor = when {
        winningNumber == 0 -> "綠色"
        redNumbers.contains(winningNumber) -> "紅色"
        else -> "黑色"
    }

    val playerWon = (betType == BetType.RED && winningColor == "紅色") || (betType == BetType.BLACK && winningColor == "黑色")

    if (playerWon) {
        financeViewModel.handleCasinoResult(bet, true, "roulette")
        onResult(Pair(winningNumber, winningColor), "你贏了！")
    } else {
        financeViewModel.handleCasinoResult(bet, false, "roulette")
        onResult(Pair(winningNumber, winningColor), "你輸了！")
    }
}
