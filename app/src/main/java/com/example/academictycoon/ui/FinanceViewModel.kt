package com.example.academictycoon.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academictycoon.data.local.dao.UserProfileDao
import com.example.academictycoon.data.local.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    init {
        viewModelScope.launch {
            _userProfile.value = userProfileDao.getUserProfile() ?: UserProfile(balance = 1000, debt = 0, correct_count = 0, rank = "學術難民")
        }
    }

    fun processReward(reward: Int) {
        viewModelScope.launch {
            val currentProfile = _userProfile.value ?: return@launch

            var newBalance = currentProfile.balance
            var newDebt = currentProfile.debt

            if (currentProfile.debt > 0) {
                val repaymentAmount = (reward * 0.8).toLong()
                val pocketAmount = reward - repaymentAmount

                val amountToPay = repaymentAmount.coerceAtMost(newDebt)
                
                newDebt -= amountToPay
                newBalance += pocketAmount + (repaymentAmount - amountToPay)
            } else {
                newBalance += reward
            }
            
            val newCorrectCount = currentProfile.correct_count + 1
            val newRank = getRank(newCorrectCount)

            val updatedProfile = currentProfile.copy(
                balance = newBalance,
                debt = newDebt,
                correct_count = newCorrectCount,
                rank = newRank
            )
            userProfileDao.insertOrUpdate(updatedProfile)
            _userProfile.value = updatedProfile
        }
    }

    fun deductBet(amount: Long) {
        viewModelScope.launch {
            val currentProfile = _userProfile.value ?: return@launch

            val updatedProfile = currentProfile.copy(balance = currentProfile.balance - amount)
            userProfileDao.insertOrUpdate(updatedProfile)
            _userProfile.value = updatedProfile
        }
    }
    
    fun borrow(amount: Long) {
        viewModelScope.launch {
            val currentProfile = _userProfile.value ?: return@launch

            val updatedProfile = currentProfile.copy(
                balance = currentProfile.balance + amount,
                debt = currentProfile.debt + amount
            )
            userProfileDao.insertOrUpdate(updatedProfile)
            _userProfile.value = updatedProfile
        }
    }

    private fun getRank(correctCount: Int): String {
        return when {
            correctCount >= 100 -> "首席機械師"
            correctCount >= 50 -> "工科兵"
            else -> "學術難民"
        }
    }
}
