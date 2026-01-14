package com.example.academictycoon.data.repository

import com.example.academictycoon.data.local.dao.QuestionDao
import com.example.academictycoon.network.ApiService
import javax.inject.Inject

class SyncRepository @Inject constructor(
    private val apiService: ApiService,
    private val questionDao: QuestionDao
) {

    suspend fun syncQuestions(url: String) {
        try {
            val bundle = apiService.getQuestionBundle(url)
            questionDao.insertAll(bundle.questions)
        } catch (e: Exception) {
            // Handle network error, e.g., by logging or notifying the user
            e.printStackTrace()
        }
    }
}
