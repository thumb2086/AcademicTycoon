package com.tycoon.academic.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tycoon.academic.data.local.dao.UserProfileDao
import com.tycoon.academic.data.local.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userProfileDao: UserProfileDao
) {

    fun getUserProfileFlow(): Flow<UserProfile?> = userProfileDao.getUserProfileFlow()

    suspend fun getUserProfile(): UserProfile? = userProfileDao.getUserProfile()

    suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.insertOrUpdate(userProfile)
        // 同步到雲端 (如果 UID 已知，這裡可以擴充同步邏輯)
    }

    /**
     * 當使用者註冊成功時，在 Firestore 建立初始存檔並同步至本地 Room
     */
    suspend fun createNewUserInCloud(uid: String, email: String) {
        val initialProfile = UserProfile(
            balance = 1000,
            debt = 0,
            rank = "學術難民"
        )

        val cloudData = hashMapOf(
            "uid" to uid,
            "email" to email,
            "balance" to initialProfile.balance,
            "debt" to initialProfile.debt,
            "rank" to initialProfile.rank,
            "correct_answers_count" to 0,
            "total_questions_answered" to 0,
            "created_at" to com.google.firebase.Timestamp.now()
        )

        try {
            // 1. 寫入 Firestore
            firestore.collection("users").document(uid)
                .set(cloudData)
                .await()

            // 2. 同步至本地資料庫 (Room)
            userProfileDao.insertOrUpdate(initialProfile)
            
        } catch (e: Exception) {
            e.printStackTrace()
            // 即使雲端失敗，至少確保本地有資料
            userProfileDao.insertOrUpdate(initialProfile)
        }
    }
}
