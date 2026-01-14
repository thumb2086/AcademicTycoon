
package com.example.academictycoon.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.academictycoon.data.local.converter.Converters
import com.example.academictycoon.data.local.dao.QuestionDao
import com.example.academictycoon.data.local.dao.UserProfileDao
import com.example.academictycoon.data.local.model.Question
import com.example.academictycoon.data.local.model.UserProfile

@Database(entities = [UserProfile::class, Question::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "academic_tycoon_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
