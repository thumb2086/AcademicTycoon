package com.tycoon.academic.data.repository

import android.util.Log
import com.tycoon.academic.data.local.dao.QuestionDao
import com.tycoon.academic.data.local.model.Question
import com.tycoon.academic.data.network.ApiService
import javax.inject.Inject

// 1. 加上 @Inject 讓 Hilt 可以注入它
class QuestionRepository @Inject constructor(
    private val apiService: ApiService,
    private val questionDao: QuestionDao
) {

    /**
     * 這是 MiningViewModel 第 36, 47 行在找的方法
     * 只負責從資料庫讀取資料
     */
    suspend fun getAllQuestions(): List<Question> {
        return questionDao.getAllQuestions()
    }

    /**
     * 這是 MiningViewModel 第 45 行在找的方法
     * 負責從網路下載並存入資料庫
     */
    suspend fun syncQuestions(url: String) {
        try {
            val questionBundle = apiService.getQuestions(url)

            // 使用 DAO 的 Transaction 方法一次完成清除與寫入
            // 如果你的 DAO 只有 deleteAll 和 insertAll，也可以分開寫
            questionDao.clearAndInsert(questionBundle.questions)

            Log.d("QuestionRepository", "Sync successful: ${questionBundle.questions.size} questions")
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Failed to fetch from network", e)
            // 這裡不拋出異常，避免 APP 崩潰，ViewModel 會繼續顯示舊資料
        }
    }
}