
package com.example.academictycoon.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val balance: Long,
    val debt: Long,
    val correct_count: Int,
    val rank: String
)
