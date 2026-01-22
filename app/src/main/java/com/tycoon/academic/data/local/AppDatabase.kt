package com.tycoon.academic.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.tycoon.academic.data.local.dao.QuestionDao
import com.tycoon.academic.data.local.dao.UserProfileDao
import com.tycoon.academic.data.local.model.Question
import com.tycoon.academic.data.local.model.UserProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(entities = [Question::class, UserProfile::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun userProfileDao(): UserProfileDao
}

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }
}
