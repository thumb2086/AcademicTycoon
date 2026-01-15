package com.example.academictycoon.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.academictycoon.data.local.dao.QuestionDao
import com.example.academictycoon.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SyncRepository @Inject constructor(
    private val apiService: ApiService,
    private val questionDao: QuestionDao,
    @param:ApplicationContext private val context: Context
) {

    suspend fun syncQuestions(url: String) {
        if (isNetworkAvailable()) {
            try {
                val bundle = apiService.getQuestionBundle(url)
                questionDao.clearAndInsert(bundle.questions) // Clear old questions before inserting new ones
            } catch (e: Exception) {
                // Handle network error, e.g., by logging
                e.printStackTrace()
            }
        } else {
            // No network, do nothing, rely on existing Room data
            println("No network connection. Using cached questions.")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
