package com.example.academictycoon.network

import com.example.academictycoon.network.dto.QuestionBundle
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getQuestionBundle(@Url url: String): QuestionBundle
}
