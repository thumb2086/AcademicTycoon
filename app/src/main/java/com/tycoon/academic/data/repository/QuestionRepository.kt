package com.tycoon.academic.data.repository

import com.tycoon.academic.data.local.dao.QuestionDao
import com.tycoon.academic.data.local.model.Question
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val questionDao: QuestionDao
) {
    /**
     * 使用 Flow 監聽資料庫變化，同步完成後 UI 會自動更新
     */
    fun getAllQuestionsFlow(): Flow<List<Question>> {
        return questionDao.getAllQuestionsFlow()
    }

    suspend fun getAllQuestions(): List<Question> {
        return questionDao.getAllQuestions()
    }
}
