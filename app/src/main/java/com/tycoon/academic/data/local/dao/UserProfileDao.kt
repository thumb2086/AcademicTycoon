package com.tycoon.academic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tycoon.academic.data.local.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(userProfile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfile(): UserProfile?

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfileFlow(): Flow<UserProfile?>
}
