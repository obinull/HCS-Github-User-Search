package dev.byto.hcsgus.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CacheCleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) { // Or extend Worker

    companion object {
        const val WORK_NAME = "CacheCleanupWorkerPeriodic"
    }

    override suspend fun doWork(): Result {
        return try {
            // Perform cleanup
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}