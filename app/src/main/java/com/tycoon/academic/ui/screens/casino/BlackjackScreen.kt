package com.tycoon.academic.ui.screens.casino

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tycoon.academic.ui.viewmodel.FinanceViewModel

@Composable
fun BlackjackScreen(
    casinoViewModel: CasinoViewModel = hiltViewModel(),
    financeViewModel: FinanceViewModel = hiltViewModel()
) {
    val uiState by casinoViewModel.uiState.collectAsState()
    val userProfile by financeViewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("21點 (Blackjack)", style = MaterialTheme.typography.headlineLarge)

        // Dealer Area
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("莊家手牌", style = MaterialTheme.typography.titleMedium)
            HandDisplay(
                cards = uiState.dealerHand,
                hideFirstCard = uiState.gameState == GameState.PLAYER_TURN
            )
            Text("點數: ${if (uiState.gameState == GameState.PLAYER_TURN) "?" else calculateHandValue(uiState.dealerHand)}")
        }

        // Result Message
        if (uiState.gameState == GameState.GAME_OVER) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.gameResult,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.gameResult.contains("Win")) Color(0xFF4CAF50) else Color.Red
                )
                Button(onClick = { casinoViewModel.endRound() }) {
                    Text("再玩一局")
                }
            }
        }

        // Player Area
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("你的手牌", style = MaterialTheme.typography.titleMedium)
            HandDisplay(cards = uiState.playerHand)
            Text("點數: ${calculateHandValue(uiState.playerHand)}")
        }

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (uiState.gameState == GameState.BETTING) {
                val bet = 100L
                val currentBalance = userProfile?.balance ?: 0L
                Button(
                    onClick = {
                        financeViewModel.deductBet(bet, "blackjack")
                        casinoViewModel.placeBet()
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    enabled = currentBalance >= bet
                ) {
                    Text("下注 $100 並開局")
                }
            } else if (uiState.gameState == GameState.PLAYER_TURN) {
                Button(onClick = { casinoViewModel.hit() }) { Text("要牌 (Hit)") }
                Button(onClick = { casinoViewModel.stand() }) { Text("停牌 (Stand)") }
            }
        }
    }

    // 當遊戲結束時處理獎勵與分析回傳
    LaunchedEffect(uiState.gameState) {
        if (uiState.gameState == GameState.GAME_OVER) {
            val isWin = uiState.gameResult.contains("Win")
            val isPush = uiState.gameResult.contains("Push")
            
            if (isWin) {
                financeViewModel.handleCasinoResult(100L, true, "blackjack")
            } else if (isPush) {
                financeViewModel.addReward(100L) // 退還本金
            } else {
                financeViewModel.handleCasinoResult(100L, false, "blackjack")
            }
        }
    }
}

@Composable
fun HandDisplay(cards: List<Card>, hideFirstCard: Boolean = false) {
    LazyRow(
        modifier = Modifier
            .height(120.dp)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards) { card ->
            val isFirst = cards.indexOf(card) == 0
            CardView(
                card = card,
                isBack = hideFirstCard && isFirst
            )
        }
    }
}

@Composable
fun CardView(card: Card, isBack: Boolean) {
    Surface(
        modifier = Modifier
            .width(70.dp)
            .fillMaxHeight(),
        shape = MaterialTheme.shapes.small,
        color = if (isBack) MaterialTheme.colorScheme.primary else Color.White,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        if (!isBack) {
            Box(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = card.toString(),
                    color = if (card.suit == Suit.HEART || card.suit == Suit.DIAMOND) Color.Red else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        } else {
            Box(contentAlignment = Alignment.Center) {
                Text("?", color = Color.White, fontSize = 24.sp)
            }
        }
    }
}
