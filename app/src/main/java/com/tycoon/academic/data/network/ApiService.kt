package com.tycoon.academic.data.network

import com.tycoon.academic.data.local.model.Question
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Url

data class QuestionBundle(
    @SerializedName("bundle_id") val bundleId: String,
    val questions: List<Question>
)

data class CasinoOdds(
    @SerializedName("blackjack_house_edge") val blackjackHouseEdge: Double,
    @SerializedName("reward_multiplier") val rewardMultiplier: Double
)

// 合併所有欄位的 AppConfig
data class AppConfig(
    @SerializedName("bundle_version") val bundleVersion: Int,    // 補上 SyncRepository 需要的
    @SerializedName("bundle_url") val bundleUrl: String,        // 補上 SyncRepository 需要的
    @SerializedName("question_bundle_url") val questionBundleUrl: String,
    @SerializedName("casino_odds") val casinoOdds: CasinoOdds
)

interface ApiService {
    @GET
    suspend fun getQuestions(@Url url: String): QuestionBundle

    @GET
    suspend fun getConfig(@Url url: String): AppConfig
}