package dev.byto.hcsgus.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CacheCleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "CacheCleanupWorkerPeriodic"
    }

    override suspend fun doWork(): Result {
        return try {
            // Perform cleanup
            Result.success()
        } catch (e: Exception) {
            Log.e(WORK_NAME, "Cleanup failed due to IOException", e)
            Result.failure()
        }
    }
}