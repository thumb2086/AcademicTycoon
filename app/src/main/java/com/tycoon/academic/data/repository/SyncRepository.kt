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
import com.tycoon.academic.data.network.QuestionBundle
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.firestore.FirebaseFirestore
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
    private val firestore: FirebaseFirestore,
    @param:ApplicationContext private val context: Context
) {

    private val _appConfig = MutableStateFlow<AppConfig?>(null)
    val appConfig = _appConfig.asStateFlow()

    suspend fun syncConfig() {
        try {
            remoteConfig.fetchAndActivate().await()
            
            val config = AppConfig(
                bundleVersion = remoteConfig.getLong("bundle_version").toInt(),
                bundleUrl = remoteConfig.getString("question_bundle_url"),
                questionBundleUrl = remoteConfig.getString("question_bundle_url"),
                casinoOdds = CasinoOdds(
                    blackjackHouseEdge = remoteConfig.getDouble("blackjack_house_edge"),
                    rewardMultiplier = remoteConfig.getDouble("reward_multiplier")
                )
            )
            _appConfig.value = config
            
            syncQuestions()
            
            Log.d("SyncRepository", "Remote Config synced: $config")
        } catch (e: Exception) {
            Log.e("SyncRepository", "Failed to sync remote config", e)
        }
    }

    suspend fun syncQuestions() {
        if (!isNetworkAvailable()) return

        val config = _appConfig.value ?: return
        val remoteVersion = config.bundleVersion
        val localVersion = preferencesRepository.bundleVersion.first()

        if (remoteVersion > localVersion) {
            Log.d("SyncRepository", "New bundle version: $remoteVersion. Syncing questions...")
            try {
                val source = config.questionBundleUrl
                
                val questions = if (source.startsWith("http")) {
                    // 方式 A: 從外部 URL 下載 (GitHub/Storage)
                    apiService.getQuestions(source).questions
                } else {
                    // 方式 B: 直接從 Firestore 讀取 (source 即為 Firestore 中的 Document ID)
                    fetchQuestionsFromFirestore(source)
                }

                if (questions != null) {
                    questionDao.clearAndInsert(questions)
                    preferencesRepository.updateBundleVersion(remoteVersion)
                    Log.d("SyncRepository", "Question sync successful: ${questions.size} items.")
                }
            } catch (e: Exception) {
                Log.e("SyncRepository", "Failed to sync questions", e)
            }
        }
    }

    /**
     * 從 Firestore 讀取題目包
     * 預期結構: Collection "question_bundles" -> Document (ID 為 source) -> Field "questions" (Array)
     */
    private suspend fun fetchQuestionsFromFirestore(docId: String): List<Question>? {
        return try {
            val document = firestore.collection("question_bundles").document(docId).get().await()
            if (document.exists()) {
                // 將 Firestore 的 Data 轉換回 Question 列表
                // 注意：Firestore 中的欄位名稱需與 Question 類別一致
                val bundle = document.toObject(QuestionBundle::class.java)
                bundle?.questions
            } else {
                Log.e("SyncRepository", "Firestore document not found: $docId")
                null
            }
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error fetching from Firestore", e)
            null
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
