package com.tycoon.academic.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.tycoon.academic.data.local.dao.QuestionDao
import com.tycoon.academic.data.local.model.Question
import com.tycoon.academic.data.network.ApiService
import com.tycoon.academic.data.network.AppConfig
import com.tycoon.academic.data.network.CasinoOdds
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val apiService: ApiService,
    private val questionDao: QuestionDao,
    private val preferencesRepository: PreferencesRepository,
    private val remoteConfig: FirebaseRemoteConfig,
    @param:ApplicationContext private val context: Context
) {

    private val _appConfig = MutableStateFlow<AppConfig?>(null)
    val appConfig = _appConfig.asStateFlow()

    // 修正後的正確 GitHub 連結：使用 _data (小寫底線) 並包含完整 refs 路徑
    private val CONFIG_JSON_URL = "https://raw.githubusercontent.com/thumb2086/AcademicTycoon_data/refs/heads/main/config.json"

    suspend fun syncConfig() {
        if (!isNetworkAvailable()) {
            Log.w("SyncRepository", "Offline: skipping sync")
            return
        }

        try {
            Log.d("SyncRepository", "Connecting to: $CONFIG_JSON_URL")
            val remoteAppConfig = apiService.getConfig(CONFIG_JSON_URL)
            
            _appConfig.value = remoteAppConfig
            
            val remoteVersion = remoteAppConfig.dataVersion
            val localVersion = preferencesRepository.bundleVersion.first()
            val currentCount = questionDao.getQuestionCount()
            
            Log.d("SyncRepository", "Sync check: LocalV=$localVersion, RemoteV=$remoteVersion, Questions=$currentCount")

            // 強制同步條件：版本不符 或 題庫為空
            if (remoteVersion != localVersion || currentCount == 0) {
                performQuestionSync(remoteAppConfig)
            } else {
                Log.d("SyncRepository", "Database is already up to date.")
            }
            
        } catch (e: Exception) {
            Log.e("SyncRepository", "Sync error (HTTP 404 means URL is wrong): ${e.message}")
        }
    }

    private suspend fun performQuestionSync(config: AppConfig) {
        val allQuestions = mutableListOf<Question>()
        Log.d("SyncRepository", "Starting batch download of ${config.bundles.size} bundles...")
        
        for (bundle in config.bundles) {
            try {
                // 直接使用 config.json 中定義的 url 欄位
                Log.d("SyncRepository", "Fetching bundle: ${bundle.name} from ${bundle.url}")
                val response = apiService.getQuestions(bundle.url)
                
                // 強制覆寫 subject，確保 UI 篩選器能正確運作
                val fixedQuestions = response.questions.map { it.copy(subject = bundle.name) }
                allQuestions.addAll(fixedQuestions)
                
                Log.d("SyncRepository", "OK: ${fixedQuestions.size} questions from ${bundle.name}")
            } catch (e: Exception) {
                Log.e("SyncRepository", "Fail: ${bundle.name} download failed. Check URL: ${bundle.url}")
            }
        }

        if (allQuestions.isNotEmpty()) {
            questionDao.clearAndInsert(allQuestions)
            preferencesRepository.updateBundleVersion(config.dataVersion)
            Log.d("SyncRepository", "Sync Success: ${allQuestions.size} questions updated.")
        } else {
            Log.e("SyncRepository", "Sync Complete but 0 questions found. Check bundle URLs in config.json.")
        }
    }

    private fun isNetworkAvailable(): Boolean = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        .run { getNetworkCapabilities(activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false }
}
