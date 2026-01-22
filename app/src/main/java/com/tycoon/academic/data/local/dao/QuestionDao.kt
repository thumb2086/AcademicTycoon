package com.tycoon.academic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tycoon.academic.data.local.model.Question

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions") // 確保 table 名稱跟你的 Entity 定義一致
    suspend fun getAllQuestions(): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("DELETE FROM questions")
    suspend fun clearAll()

    @Transaction
    suspend fun clearAndInsert(questions: List<Question>) {
        clearAll()
        insertAll(questions)
    }
}