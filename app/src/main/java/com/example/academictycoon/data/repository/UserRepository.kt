
package com.example.academictycoon.data.repository

import com.example.academictycoon.data.local.dao.UserProfileDao
import com.example.academictycoon.data.local.model.UserProfile
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userProfileDao: UserProfileDao) {

    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.insertOrUpdate(userProfile)
    }
}
