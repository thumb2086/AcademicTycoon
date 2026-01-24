package com.tycoon.academic.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 正式環境建議 1 小時，開發時可調低
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        // 預設值設定
        val defaultValues = mapOf(
            "bundle_version" to 1,
            "question_bundle_url" to "https://raw.githubusercontent.com/thumb2086/AcademicTycoon/main/app/src/main/assets/mechanical.json",
            "blackjack_house_edge" to 0.5,
            "reward_multiplier" to 1.0
        )
        remoteConfig.setDefaultsAsync(defaultValues)
        return remoteConfig
    }
}
