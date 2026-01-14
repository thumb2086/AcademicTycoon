package com.example.academictycoon.network.dto

import com.example.academictycoon.data.local.model.Question

data class QuestionBundle(
    val bundle_id: String,
    val questions: List<Question>
)
