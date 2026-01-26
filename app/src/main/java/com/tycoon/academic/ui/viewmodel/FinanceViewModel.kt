package com.tycoon.academic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tycoon.academic.data.local.model.UserProfile
import com.tycoon.academic.data.repository.UserRepository
import com.tycoon.academic.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    val appConfig = syncRepository.appConfig

    val userProfile: StateFlow<UserProfile?> = userRepository.getUserProfileFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        ensureUserExists()
        // 啟動時強制進行一次遠端同步，確保提取 GitHub 上的新題庫
        viewModelScope.launch {
            syncRepository.syncConfig()
        }
    }

    private fun ensureUserExists() {
        viewModelScope.launch {
            val user = userRepository.getUserProfile()
            if (user == null) {
                userRepository.updateUserProfile(UserProfile(balance = 1000, debt = 0, rank = "學術難民"))
            }
        }
    }
    
    // ... 其餘財務與頭銜計算邏輯保持不變 ...
    
    fun addReward(reward: Long, fromQuestion: Boolean = false) {
        viewModelScope.launch {
            val user = userRepository.getUserProfile() ?: return@launch
            
            if (fromQuestion) {
                updateAnalytics(isCorrect = true)
            }

            if (user.debt > 0) {
                val payBack = (reward * 0.8).toLong()
                val pocket = (reward * 0.2).toLong()
                var remainingDebt = user.debt - payBack
                var finalBalance = user.balance + pocket
                if (remainingDebt < 0) {
                    finalBalance += (-remainingDebt)
                    remainingDebt = 0
                }
                userRepository.updateUserProfile(user.copy(balance = finalBalance, debt = remainingDebt))
            } else {
                userRepository.updateUserProfile(user.copy(balance = user.balance + reward))
            }
        }
    }

    fun recordWrongAnswer() {
        updateAnalytics(isCorrect = false)
    }

    private fun calculateRank(correctCount: Int): String {
        return when {
            correctCount >= 1000 -> "諾貝爾獎得主"
            correctCount >= 500 -> "終身教授"
            correctCount >= 250 -> "正教授"
            correctCount >= 100 -> "副教授"
            correctCount >= 50 -> "助理教授"
            correctCount >= 20 -> "博士候選人"
            correctCount >= 5 -> "研究生"
            else -> "學術難民"
        }
    }

    private fun updateAnalytics(isCorrect: Boolean? = null, isWin: Boolean? = null, gameType: String? = null, bet: Long = 0) {
        viewModelScope.launch {
            val user = userRepository.getUserProfile() ?: return@launch
            var updated = user.copy(total_bet_amount = user.total_bet_amount + bet)
            
            if (isCorrect != null) {
                val newCorrectCount = if (isCorrect) user.correct_answers_count + 1 else user.correct_answers_count
                updated = updated.copy(
                    total_questions_answered = user.total_questions_answered + 1,
                    correct_answers_count = newCorrectCount,
                    rank = calculateRank(newCorrectCount)
                )
            }
            
            if (isWin != null && gameType != null) {
                updated = when (gameType) {
                    "blackjack" -> if (isWin) updated.copy(blackjack_wins = user.blackjack_wins + 1) else updated.copy(blackjack_losses = user.blackjack_losses + 1)
                    "roulette" -> if (isWin) updated.copy(roulette_wins = user.roulette_wins + 1) else updated.copy(roulette_losses = user.roulette_losses + 1)
                    else -> updated
                }
            }
            userRepository.updateUserProfile(updated)
        }
    }

    fun deductBet(amount: Long, gameType: String) {
        viewModelScope.launch {
            val user = userRepository.getUserProfile() ?: return@launch
            userRepository.updateUserProfile(user.copy(balance = (user.balance - amount).coerceAtLeast(0)))
            updateAnalytics(bet = amount)
        }
    }

    fun handleCasinoResult(bet: Long, isWin: Boolean, gameType: String) {
        val multiplier = appConfig.value?.casinoOdds?.rewardMultiplier ?: 2.0
        if (isWin) {
            val reward = (bet * multiplier).toLong()
            addReward(reward)
            updateAnalytics(isWin = true, gameType = gameType)
        } else {
            updateAnalytics(isWin = false, gameType = gameType)
        }
    }

    fun borrow(amount: Long) = viewModelScope.launch {
        val user = userRepository.getUserProfile() ?: return@launch
        userRepository.updateUserProfile(user.copy(balance = user.balance + amount, debt = user.debt + amount))
    }

    fun repayDebt(amount: Long) = viewModelScope.launch {
        val user = userRepository.getUserProfile() ?: return@launch
        val pay = if (amount > user.debt) user.debt else amount
        if (user.balance >= pay) {
            userRepository.updateUserProfile(user.copy(balance = user.balance - pay, debt = user.debt - pay))
        }
    }
}
