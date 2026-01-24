package com.tycoon.academic.data

import com.google.firebase.auth.FirebaseAuth
import com.tycoon.academic.data.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) {

    fun getCurrentUser() = auth.currentUser

    suspend fun signIn(email: String, password: String) = auth.signInWithEmailAndPassword(email, password).await()

    suspend fun signUp(email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) {
            // 註冊成功，建立雲端初始存檔
            userRepository.createNewUserInCloud(user.uid, email)
        }
    }

    fun signOut() = auth.signOut()
}
