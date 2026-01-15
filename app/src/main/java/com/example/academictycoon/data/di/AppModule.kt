package com.example.academictycoon.data.di

import android.content.Context
import androidx.room.Room
import com.example.academictycoon.data.local.AppDatabase
import com.example.academictycoon.data.local.dao.QuestionDao
import com.example.academictycoon.data.local.dao.UserProfileDao
import com.example.academictycoon.data.repository.QuestionRepository
import com.example.academictycoon.data.repository.UserRepository
import com.example.academictycoon.data.network.ApiService
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "academic_tycoon_db"
        ).build()
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
            .baseUrl("https://raw.githubusercontent.com/") // Ensure this is your actual base URL
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
    fun provideUserRepository(userProfileDao: UserProfileDao): UserRepository {
        return UserRepository(userProfileDao)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(apiService: ApiService, questionDao: QuestionDao): QuestionRepository {
        return QuestionRepository(apiService, questionDao)
    }
}