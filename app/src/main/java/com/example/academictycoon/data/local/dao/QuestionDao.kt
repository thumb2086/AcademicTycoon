
package com.example.academictycoon.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.academictycoon.data.local.model.Question

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<Question>

    @Query("DELETE FROM questions")
    suspend fun clearAll()
}
