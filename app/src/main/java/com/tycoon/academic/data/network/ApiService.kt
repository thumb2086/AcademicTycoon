package com.tycoon.academic.data.network

import com.tycoon.academic.data.local.model.Question
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Url

data class QuestionBundle(
    @SerializedName("bundle_id") val bundleId: String,
    val questions: List<Question>
)

data class DataBundle(
    val id: String,
    val name: String,
    @SerializedName("file_name") val fileName: String,
    val url: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class CasinoOdds(
    @SerializedName("blackjack_house_edge") val blackjackHouseEdge: Double,
    @SerializedName("reward_multiplier") val rewardMultiplier: Double
)

// 更新後的 AppConfig，符合新的 config.json 結構
data class AppConfig(
    @SerializedName("app_version_required") val appVersionRequired: String? = null,
    @SerializedName("data_version") val dataVersion: Int = 0,
    val bundles: List<DataBundle> = emptyList(),
    @SerializedName("casino_odds") val casinoOdds: CasinoOdds? = null
)

interface ApiService {
    @GET
    suspend fun getQuestions(@Url url: String): QuestionBundle

    @GET
    suspend fun getConfig(@Url url: String): AppConfig
}
