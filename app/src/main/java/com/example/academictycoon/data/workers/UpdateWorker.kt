package com.example.academictycoon.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.academictycoon.data.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // First, sync the remote configuration
            syncRepository.syncConfig()

            // Then, use the config to get the questions bundle URL
            val config = syncRepository.appConfig.firstOrNull()
            val questionUrl = config?.questionBundleUrl

            if (questionUrl != null) {
                // Sync questions using the URL from the config
                syncRepository.syncQuestions(questionUrl)
                Result.success()
            } else {
                // If config or URL is null, retry later
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
