package com.tycoon.academic.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tycoon.academic.data.local.AppDatabase
import com.tycoon.academic.data.local.dao.QuestionDao
import com.tycoon.academic.data.local.dao.UserProfileDao
import com.tycoon.academic.data.network.ApiService
import com.tycoon.academic.data.repository.QuestionRepository
import com.tycoon.academic.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "academic_tycoon_db"
        )
        .fallbackToDestructiveMigration()
        .build()
        // 移除了 onCreate 中的 Assets 預載入邏輯，現在完全由遠端同步
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(appDatabase: AppDatabase): UserProfileDao = appDatabase.userProfileDao()

    @Provides
    @Singleton
    fun provideQuestionDao(appDatabase: AppDatabase): QuestionDao = appDatabase.questionDao()

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        userProfileDao: UserProfileDao
    ): UserRepository {
        return UserRepository(firestore, auth, userProfileDao)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(
        questionDao: QuestionDao
    ): QuestionRepository {
        return QuestionRepository(questionDao)
    }
}
