package com.example.academictycoon.data.network

import com.example.academictycoon.data.local.model.Question
import retrofit2.http.GET

data class QuestionBundle(
    val bundle_id: String,
    val questions: List<Question>
)

interface ApiService {
    @GET("https://raw.githubusercontent.com/user/repo/main/bundle.json")
    suspend fun getQuestions(): QuestionBundle
}
