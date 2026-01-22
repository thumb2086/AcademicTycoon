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
        viewModelScope.launch {
            var user = userRepository.getUserProfile()
            if (user == null) {
                user = UserProfile(balance = 1000, debt = 0, correct_count = 0, rank = "學術難民")
                userRepository.updateUserProfile(user)
            }
            _userProfile.value = user
        }
    }

    fun addReward(reward: Long) {
        viewModelScope.launch {
            val currentUser = _userProfile.value ?: return@launch

            if (currentUser.debt > 0) {
                val payBack = reward * 8 / 10
                val pocket = reward * 2 / 10

                val remainingDebt = currentUser.debt - payBack
                if (remainingDebt <= 0) {
                    // Debt is paid off, add remainder to balance
                    val newBalance = currentUser.balance + pocket + -remainingDebt
                    val updatedProfile = currentUser.copy(balance = newBalance, debt = 0)
                    userRepository.updateUserProfile(updatedProfile)
                    _userProfile.value = updatedProfile
                } else {
                    // Still in debt
                    val newBalance = currentUser.balance + pocket
                    val updatedProfile = currentUser.copy(balance = newBalance, debt = remainingDebt)
                    userRepository.updateUserProfile(updatedProfile)
                    _userProfile.value = updatedProfile
                }
            } else {
                // No debt, full reward goes to balance
                val newBalance = currentUser.balance + reward
                val updatedProfile = currentUser.copy(balance = newBalance)
                userRepository.updateUserProfile(updatedProfile)
                _user_profile.value = updatedProfile
            }
        }
    }
}
