package com.tycoon.academic.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tycoon.academic.data.local.dao.UserProfileDao
import com.tycoon.academic.data.local.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userProfileDao: UserProfileDao
) {

    fun getUserProfileFlow(): Flow<UserProfile?> = userProfileDao.getUserProfileFlow()

    suspend fun getUserProfile(): UserProfile? = userProfileDao.getUserProfile()

    /**
     * 更新使用者資料，同時同步至 Room 與 Firestore
     */
    suspend fun updateUserProfile(userProfile: UserProfile) {
        // 1. 更新本地資料庫
        userProfileDao.insertOrUpdate(userProfile)
        
        // 2. 同步至 Firebase Firestore (若已登入)
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                // 將 UserProfile 轉換為 Map 以便 Firestore 存儲，或直接傳入對象
                // 排除 Room 的 id 欄位或將其作為文檔一部分
                firestore.collection("users").document(uid)
                    .set(userProfile)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
                // 雲端同步失敗時，至少本地已有資料
            }
        }
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
