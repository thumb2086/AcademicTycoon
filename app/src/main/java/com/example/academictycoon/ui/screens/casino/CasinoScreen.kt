package com.example.academictycoon.ui.screens.casino

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.academictycoon.ui.FinanceViewModel

@Composable
fun CasinoScreen(
    casinoViewModel: CasinoViewModel = hiltViewModel(),
    financeViewModel: FinanceViewModel = hiltViewModel()
) {
    val uiState by casinoViewModel.uiState.collectAsState()
    val userProfile by financeViewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Dealer's Hand
        HandView(title = "Dealer's Hand", cards = uiState.dealerHand, isDealer = true, gameState = uiState.gameState)

        // Player's Hand
        HandView(title = "Your Hand", cards = uiState.playerHand)

        // Game Status & Actions
        if (uiState.gameState == GameState.GAME_OVER) {
            Text(text = uiState.gameResult, modifier = Modifier.padding(bottom = 16.dp))
            Button(onClick = {
                val winnings = casinoViewModel.getWinnings()
                if (winnings > 0) {
                    financeViewModel.processReward(winnings.toInt())
                }
                casinoViewModel.endRound()
            }) {
                Text("New Round")
            }
        } else if (uiState.gameState == GameState.BETTING) {
            Button(onClick = {
                userProfile?.let { casinoViewModel.placeBet(it) { amount -> financeViewModel.processReward(amount.toInt()) } }
            }) {
                Text("Place Bet: ${uiState.betAmount}")
            }
        } else {
            Row {
                Button(onClick = { casinoViewModel.hit() }, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Hit")
                }
                Button(onClick = { casinoViewModel.stand() }) {
                    Text("Stand")
                }
            }
        }
    }
}

@Composable
fun HandView(title: String, cards: List<Card>, isDealer: Boolean = false, gameState: GameState = GameState.PLAYER_TURN) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            cards.forEachIndexed { index, card ->
                val cardToShow = if (isDealer && gameState == GameState.PLAYER_TURN && index == 1) "??" else card.toString()
                Card(modifier = Modifier.padding(4.dp)) {
                    Text(text = cardToShow, modifier = Modifier.padding(8.dp))
                }
            }
        }
        val handValue = if (isDealer && gameState == GameState.PLAYER_TURN) "" else "(${calculateHandValue(cards)})"
        Text(text = "Value: $handValue")
    }
}
