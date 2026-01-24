package com.tycoon.academic.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tycoon.academic.data.local.AppDatabase
import com.tycoon.academic.data.local.dao.QuestionDao
import com.tycoon.academic.data.local.dao.UserProfileDao
import com.tycoon.academic.data.network.ApiService
import com.tycoon.academic.data.network.QuestionBundle
import com.tycoon.academic.data.repository.QuestionRepository
import com.tycoon.academic.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        questionDaoProvider: Provider<QuestionDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "academic_tycoon_db"
        )
        .fallbackToDestructiveMigration()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Pre-populate the database using a coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val jsonString = context.assets.open("bundle.json").bufferedReader().use { it.readText() }
                        val questionBundle = Gson().fromJson(jsonString, QuestionBundle::class.java)
                        questionDaoProvider.get().insertAll(questionBundle.questions)
                    } catch (e: Exception) {
                        // Handle exceptions, e.g., file not found or JSON parsing error
                    }
                }
            }
        }).build()
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
        userProfileDao: UserProfileDao
    ): UserRepository {
        return UserRepository(firestore, userProfileDao)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(
        apiService: ApiService, 
        questionDao: QuestionDao,
        @ApplicationContext context: Context
    ): QuestionRepository {
        return QuestionRepository(apiService, questionDao, context)
    }
}
