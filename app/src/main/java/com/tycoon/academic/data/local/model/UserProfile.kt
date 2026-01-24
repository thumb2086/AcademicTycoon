package com.tycoon.academic.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val balance: Long,
    val debt: Long,
    val rank: String,
    
    // --- 後台分析與答對率紀錄 ---
    val total_questions_answered: Int = 0,
    val correct_answers_count: Int = 0,
    val blackjack_wins: Int = 0,
    val blackjack_losses: Int = 0,
    val roulette_wins: Int = 0,
    val roulette_losses: Int = 0,
    val total_bet_amount: Long = 0
) {
    // 計算答對率 (百分比)
    val accuracy: Float
        get() = if (total_questions_answered > 0) {
            (correct_answers_count.toFloat() / total_questions_answered.toFloat()) * 100f
        } else 0f
}
