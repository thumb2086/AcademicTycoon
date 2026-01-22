package com.tycoon.academic.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val balance: Long,
    val debt: Long,
    val correct_count: Int,
    val rank: String
)
