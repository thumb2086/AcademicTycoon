package com.tycoon.academic.data.repository

import android.content.Context
import android.util.Log
import com.tycoon.academic.data.local.dao.QuestionDao
import com.tycoon.academic.data.local.model.Question
import com.tycoon.academic.data.network.ApiService
import com.tycoon.academic.data.network.QuestionBundle
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val apiService: ApiService,
    private val questionDao: QuestionDao,
    @ApplicationContext private val context: Context
) {

    suspend fun getAllQuestions(): List<Question> {
        return questionDao.getAllQuestions()
    }

    suspend fun syncQuestions(url: String) {
        try {
            val questionBundle = if (url.startsWith("http")) {
                apiService.getQuestions(url)
            } else {
                // 如果不是 http 開頭，嘗試從 Assets 讀取
                loadQuestionsFromAssets(url)
            }

            questionBundle?.let {
                questionDao.clearAndInsert(it.questions)
                Log.d("QuestionRepository", "Sync successful: ${it.questions.size} questions")
            }
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Failed to sync questions from $url", e)
        }
    }

    private fun loadQuestionsFromAssets(fileName: String): QuestionBundle? {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            Gson().fromJson(jsonString, QuestionBundle::class.java)
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Error loading from assets: $fileName", e)
            null
        }
    }
}
