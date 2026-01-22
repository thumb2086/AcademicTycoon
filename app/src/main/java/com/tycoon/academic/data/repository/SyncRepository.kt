package com.tycoon.academic.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.tycoon.academic.data.local.dao.QuestionDao
import com.tycoon.academic.data.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import com.tycoon.academic.data.network.AppConfig // 增加這一行

@Singleton
class SyncRepository @Inject constructor(
    private val apiService: ApiService,
    private val questionDao: QuestionDao,
    private val preferencesRepository: PreferencesRepository,
    @param:ApplicationContext private val context: Context
) {

    private val _appConfig = MutableStateFlow<AppConfig?>(null)
    val appConfig = _appConfig.asStateFlow()

    suspend fun syncConfig(url: String = "https://raw.githubusercontent.com/thumb2086/AcademicTycoon/main/app/src/main/assets/config.json") {
        if (isNetworkAvailable()) {
            try {
                _appConfig.value = apiService.getConfig(url)
            } catch (e: Exception) {
                Log.e("SyncRepository", "Failed to fetch remote config", e)
            }
        }
    }

    suspend fun syncQuestions() {
        if (!isNetworkAvailable()) {
            println("No network connection, skipping question sync.")
            return
        }

        val remoteConfig = appConfig.value ?: return

        // --- 修正點：將 snake_case 改為 camelCase ---
        val remoteVersion = remoteConfig.bundleVersion // 修正: bundle_version -> bundleVersion
        val localVersion = preferencesRepository.bundleVersion.first()

        if (remoteVersion > localVersion) {
            Log.d("SyncRepository", "New bundle version found: $remoteVersion. Local was: $localVersion. Syncing...")
            try {
                // --- 修正點：將 snake_case 改為 camelCase ---
                val bundle = apiService.getQuestions(remoteConfig.bundleUrl) // 修正: bundle_url -> bundleUrl
                questionDao.clearAndInsert(bundle.questions)
                preferencesRepository.updateBundleVersion(remoteVersion)
                Log.d("SyncRepository", "Sync successful.")
            } catch (e: Exception) {
                Log.e("SyncRepository", "Failed to sync questions", e)
            }
        } else {
            Log.d("SyncRepository", "Questions are up to date. Local version: $localVersion, Remote version: $remoteVersion")
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