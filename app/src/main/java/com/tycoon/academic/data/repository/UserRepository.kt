package com.tycoon.academic.data.repository

import com.tycoon.academic.data.local.dao.UserProfileDao
import com.tycoon.academic.data.local.model.UserProfile
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userProfileDao: UserProfileDao) {

    // This should return a Flow, but the DAO is not set up for it.
    // For now, we will make this a suspend function.
    suspend fun getUserProfile(): UserProfile? = userProfileDao.getUserProfile()

    suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.insertOrUpdate(userProfile)
    }
}
