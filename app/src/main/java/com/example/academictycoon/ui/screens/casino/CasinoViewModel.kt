package com.example.academictycoon.ui.screens.casino

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class GameState {
    BETTING, PLAYER_TURN, DEALER_TURN, GAME_OVER
}

data class CasinoUiState(
    val playerHand: List<Card> = emptyList(),
    val dealerHand: List<Card> = emptyList(),
    val gameState: GameState = GameState.BETTING,
    val gameResult: String = "",
    val betAmount: Long = 100
)

@HiltViewModel
class CasinoViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CasinoUiState())
    val uiState: StateFlow<CasinoUiState> = _uiState

    private val deck = Deck()

    fun placeBet() {
        startNewGame()
    }

    private fun startNewGame() {
        deck.reset()
        val playerHand = listOf(deck.drawCard(), deck.drawCard())
        val dealerHand = listOf(deck.drawCard(), deck.drawCard())

        _uiState.value = _uiState.value.copy(
            playerHand = playerHand,
            dealerHand = dealerHand,
            gameState = GameState.PLAYER_TURN,
            gameResult = ""
        )

        if (calculateHandValue(playerHand) == 21) {
            stand()
        }
    }

    fun hit() {
        if (_uiState.value.gameState != GameState.PLAYER_TURN) return

        val newPlayerHand = _uiState.value.playerHand.toMutableList().apply { add(deck.drawCard()) }
        _uiState.value = _uiState.value.copy(playerHand = newPlayerHand)

        if (calculateHandValue(newPlayerHand) > 21) {
            _uiState.value = _uiState.value.copy(gameResult = "You Bust!", gameState = GameState.GAME_OVER)
        }
    }

    fun stand() {
        if (_uiState.value.gameState != GameState.PLAYER_TURN) return

        _uiState.value = _uiState.value.copy(gameState = GameState.DEALER_TURN)
        dealerTurn()
    }

    private fun dealerTurn() {
        var dealerHand = _uiState.value.dealerHand
        while (calculateHandValue(dealerHand) < 17) {
            dealerHand = dealerHand.toMutableList().apply { add(deck.drawCard()) }
        }
        _uiState.value = _uiState.value.copy(dealerHand = dealerHand)
        determineWinner()
    }

    private fun determineWinner() {
        val playerValue = calculateHandValue(_uiState.value.playerHand)
        val dealerValue = calculateHandValue(_uiState.value.dealerHand)

        val result = when {
            dealerValue > 21 -> "Dealer Busts! You Win!"
            playerValue > dealerValue -> "You Win!"
            dealerValue == playerValue -> "Push!"
            else -> "You Lose!"
        }
        _uiState.value = _uiState.value.copy(gameResult = result, gameState = GameState.GAME_OVER)
    }

    fun endRound() {
        _uiState.value = _uiState.value.copy(gameState = GameState.BETTING)
    }
}
