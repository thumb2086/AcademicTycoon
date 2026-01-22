package com.tycoon.academic.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: String,
    val subject: String,
    val q: String,
    val options: List<String>,
    val a: Int,
    val reward: Int,
    val explanation: String,
    val image_url: String
)
