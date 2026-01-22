package com.tycoon.academic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tycoon.academic.data.local.model.UserProfile
import com.tycoon.academic.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            var user = userRepository.getUserProfile()
            if (user == null) {
                user = UserProfile(balance = 1000, debt = 0, correct_count = 0, rank = "學術難民")
                userRepository.updateUserProfile(user)
            }
            _userProfile.value = user
        }
    }

    // --- 賭場與財務邏輯開始 ---

    /**
     * 扣除下注金額
     */
    fun deductBet(amount: Long) {
        viewModelScope.launch {
            val currentUser = _userProfile.value ?: return@launch
            // 確保餘額不會扣到負數（雖然畫面上應該攔截，但 ViewModel 也要保險）
            val newBalance = (currentUser.balance - amount).coerceAtLeast(0)
            val updatedProfile = currentUser.copy(balance = newBalance)

            userRepository.updateUserProfile(updatedProfile)
            _userProfile.value = updatedProfile
        }
    }

    /**
     * 處理賭場贏錢 (通常是 2 倍回報)
     * 這裡我們直接調用 addReward，這樣贏來的錢也會自動套用 80/20 償債規則
     */
    fun handleCasinoWin(bet: Long) {
        val reward = bet * 2
        addReward(reward)
    }

    /**
     * 增加獎勵，並套用 80% 償債 / 20% 入袋規則
     */
    fun addReward(reward: Long) {
        viewModelScope.launch {
            val currentUser = _userProfile.value ?: return@launch

            if (currentUser.debt > 0) {
                val payBack = reward * 8 / 10  // 80% 還債
                val pocket = reward * 2 / 10   // 20% 進口袋

                var remainingDebt = currentUser.debt - payBack
                var finalBalance = currentUser.balance + pocket

                if (remainingDebt < 0) {
                    // 債務還清了，溢出的還債金額回到餘額
                    finalBalance += (-remainingDebt)
                    remainingDebt = 0
                }

                val updatedProfile = currentUser.copy(balance = finalBalance, debt = remainingDebt)
                userRepository.updateUserProfile(updatedProfile)
                _userProfile.value = updatedProfile // 修正點：之前這裡寫成 _user_profile
            } else {
                // 沒債務，全額進入餘額
                val newBalance = currentUser.balance + reward
                val updatedProfile = currentUser.copy(balance = newBalance)
                userRepository.updateUserProfile(updatedProfile)
                _userProfile.value = updatedProfile // 修正點：之前這裡寫成 _user_profile
            }
        }
    }
    /**
     * 借貸邏輯：增加餘額的同時增加債務
     */
    fun borrow(amount: Long) {
        viewModelScope.launch {
            val currentUser = _userProfile.value ?: return@launch
            val updatedProfile = currentUser.copy(
                balance = currentUser.balance + amount,
                debt = currentUser.debt + amount
            )
            userRepository.updateUserProfile(updatedProfile)
            _userProfile.value = updatedProfile
        }
    }

    /**
     * 手動還債邏輯
     */
    fun repayDebt(amount: Long) {
        viewModelScope.launch {
            val currentUser = _userProfile.value ?: return@launch
            if (currentUser.balance < amount) return@launch // 餘額不足

            val payAmount = if (amount > currentUser.debt) currentUser.debt else amount
            val updatedProfile = currentUser.copy(
                balance = currentUser.balance - payAmount,
                debt = currentUser.debt - payAmount
            )
            userRepository.updateUserProfile(updatedProfile)
            _userProfile.value = updatedProfile
        }
    }
}