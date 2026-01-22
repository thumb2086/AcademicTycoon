package com.tycoon.academic.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun getCurrentUser() = auth.currentUser

    suspend fun signIn(email: String, password: String) = auth.signInWithEmailAndPassword(email, password).await()

    suspend fun signUp(email: String, password: String) = auth.createUserWithEmailAndPassword(email, password).await()

    fun signOut() = auth.signOut()
}
