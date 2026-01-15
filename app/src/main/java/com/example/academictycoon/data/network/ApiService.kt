package com.example.academictycoon.data.network

import com.example.academictycoon.data.local.model.Question
import retrofit2.http.GET
import retrofit2.http.Url

data class QuestionBundle(
    val bundle_id: String,
    val questions: List<Question>
)

interface ApiService {
    @GET
    suspend fun getQuestions(@Url url: String): QuestionBundle
}
