package dev.byto.hcsgus

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.byto.hcsgus.util.CacheCleanupWorker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class GithubSearchAppTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var workerFactory: androidx.hilt.work.HiltWorkerFactory

    private lateinit var context: Context
    private lateinit var workManager: WorkManager

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext<GithubSearchApp>()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .setWorkerFactory(workerFactory)
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun verifyWorkManagerConfiguration() {
        val app = ApplicationProvider.getApplicationContext<GithubSearchApp>()
        val configuration = app.workManagerConfiguration
        assertNotNull(configuration.workerFactory)
        assertTrue(configuration.workerFactory is androidx.hilt.work.HiltWorkerFactory)
    }

    @Test
    fun verifyPeriodicWorkIsEnqueued() {
        // The work is enqueued in Application's onCreate, which runs before this test.
        // We just need to check if it was enqueued correctly.
        val workInfos = workManager.getWorkInfosForUniqueWork(CacheCleanupWorker.WORK_NAME).get()
        assertNotNull(workInfos)
        assertTrue(workInfos.isNotEmpty())

        val workInfo = workInfos[0]
        assertEquals(WorkInfo.State.ENQUEUED, workInfo.state)

        // Optionally, check if the period is correct (1 day)
        // This requires accessing internal details of the WorkRequest or checking against the known period
        // For simplicity, we're focusing on it being enqueued with the correct name.
    }
}
