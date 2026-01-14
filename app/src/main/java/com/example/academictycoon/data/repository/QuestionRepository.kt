package com.example.academictycoon.data.repository

import android.util.Log
import com.example.academictycoon.data.local.dao.QuestionDao
import com.example.academictycoon.data.local.model.Question
import com.example.academictycoon.data.network.ApiService

class QuestionRepository(
    private val apiService: ApiService,
    private val questionDao: QuestionDao
) {

    suspend fun getQuestions(isOnline: Boolean): List<Question> {
        return if (isOnline) {
            try {
                val questionBundle = apiService.getQuestions()
                questionDao.clearAll()
                questionDao.insertAll(questionBundle.questions)
                questionBundle.questions
            } catch (e: Exception) {
                Log.e("QuestionRepository", "Failed to fetch from network, falling back to local cache.", e)
                questionDao.getAllQuestions()
            }
        } else {
            questionDao.getAllQuestions()
        }
    }
}