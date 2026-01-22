package com.tycoon.academic.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tycoon.academic.data.local.dao.UserProfileDao
import com.tycoon.academic.data.local.model.UserProfile
import com.tycoon.academic.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val syncRepository: SyncRepository // Injected SyncRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    init {
        viewModelScope.launch {
            syncRepository.syncConfig()
            _userProfile.value = userProfileDao.getUserProfile() ?: UserProfile(balance = 1000, debt = 0, correct_count = 0, rank = "學術難民")
        }
    }

    private fun applyIncomeWithDebtRule(profile: UserProfile, income: Long): UserProfile {
        var newBalance = profile.balance
        var newDebt = profile.debt

        if (profile.debt > 0) {
            val repaymentAmount = (income * 0.8).toLong()
            val pocketAmount = income - repaymentAmount
            val amountToPay = repaymentAmount.coerceAtMost(newDebt)
            
            newDebt -= amountToPay
            newBalance += pocketAmount + (repaymentAmount - amountToPay)
        } else {
            newBalance += income
        }
        return profile.copy(balance = newBalance, debt = newDebt)
    }

    fun processReward(reward: Int) {
        val currentProfile = _userProfile.value ?: return
        var updatedProfile = applyIncomeWithDebtRule(currentProfile, reward.toLong())
        
        val newCorrectCount = currentProfile.correct_count + 1
        val newRank = getRank(newCorrectCount)

        updatedProfile = updatedProfile.copy(
            correct_count = newCorrectCount,
            rank = newRank
        )
        updateProfile(updatedProfile)
    }

    fun handleCasinoWin(bet: Long) {
        viewModelScope.launch {
            val currentProfile = _userProfile.value ?: return@launch
            val config = syncRepository.appConfig.firstOrNull()
            val multiplier = config?.casinoOdds?.rewardMultiplier ?: 1.5 
            val payout = (bet * multiplier).toLong()
            val updatedProfile = applyIncomeWithDebtRule(currentProfile, payout)
            updateProfile(updatedProfile)
        }
    }

    fun returnBet(bet: Long) {
        val currentProfile = _userProfile.value ?: return
        val updatedProfile = currentProfile.copy(balance = currentProfile.balance + bet)
        updateProfile(updatedProfile)
    }

    fun deductBet(amount: Long) {
        val currentProfile = _userProfile.value ?: return
        val updatedProfile = currentProfile.copy(balance = currentProfile.balance - amount)
        updateProfile(updatedProfile)
    }
    
    fun borrow(amount: Long) {
        val currentProfile = _userProfile.value ?: return
        val updatedProfile = currentProfile.copy(
            balance = currentProfile.balance + amount,
            debt = currentProfile.debt + amount
        )
        updateProfile(updatedProfile)
    }

    fun repayDebt(amount: Long) {
        val currentProfile = _userProfile.value ?: return
        val amountToRepay = amount.coerceAtMost(currentProfile.balance).coerceAtMost(currentProfile.debt)
        if (amountToRepay <= 0) return
        val updatedProfile = currentProfile.copy(
            balance = currentProfile.balance - amountToRepay,
            debt = currentProfile.debt - amountToRepay
        )
        updateProfile(updatedProfile)
    }

    private fun updateProfile(updatedProfile: UserProfile) {
        viewModelScope.launch {
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
