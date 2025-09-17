package dev.byto.hcsgus.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.byto.hcsgus.BuildConfig
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.dao.RemoteKeysDao
import dev.byto.hcsgus.data.local.dao.UserDao
import dev.byto.hcsgus.util.constant.DatabaseConstant
import net.sqlcipher.database.SupportFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DatabaseModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var remoteKeysDao: RemoteKeysDao

    @Inject
    lateinit var context: Context // For recreating DB if needed for specific checks

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testAppDatabaseProvidedAndConfigured() {
        assertNotNull("AppDatabase should be injected", appDatabase)

        // Check database name (requires accessing internal properties or testing behavior)
        // A simple way is to check if it's open and queryable.
        assertTrue("Database should be open", appDatabase.isOpen)

        // In test, BuildConfig.DEBUG is usually true, so DB should NOT be encrypted.
        // The default factory is FrameworkSQLiteOpenHelperFactory if no other is set.
        // If SQLCipher's SupportFactory was used, the openHelperFactory would be an instance of it.
        // This is a bit indirect. A more direct check on the builder inside the module would be ideal,
        // but we test the outcome of the Hilt-provided instance.

        // To check the factory, we might have to re-evaluate how AppDatabase exposes this or build a test instance.
        // For now, we assume if DEBUG is true, no SupportFactory is set.
        if (BuildConfig.DEBUG) {
            // We can't directly access the openHelperFactory from the Hilt-provided instance.
            // We can, however, verify by trying to open it without a passphrase.
            // If it were encrypted, this would fail or require a passphrase.
            // As a proxy, we check if we can perform a simple query.
            try {
                appDatabase.query("SELECT 1", null)
            } catch (e: Exception) {
                throw AssertionError("Database should be queryable without passphrase in DEBUG mode", e)
            }
        } else {
            // If we could somehow force BuildConfig.DEBUG to false in this test environment,
            // we would check for SupportFactory or try to open with a passphrase.
            // This part is harder to test in isolation without specific test configurations for build types.
        }

        // Test database name (this is a bit of a hack, assumes DB is closed and then check path)
        // This is not directly testable from the instance easily for the DB name provided at build time.
        // Better to trust the module code for the name, but we can verify it's the one expected for the app.
        // No, can't get databaseName directly if already created by Hilt.
        // Let's rely on other tests (DAO tests) to confirm DB is working.
    }

    @Test
    fun testUserDaoProvided() {
        assertNotNull("UserDao should be injected", userDao)
        // Check if it's the instance from our AppDatabase
        assertEquals("UserDao should be from the injected AppDatabase", appDatabase.userDao(), userDao)
    }

    @Test
    fun testRemoteKeysDaoProvided() {
        assertNotNull("RemoteKeysDao should be injected", remoteKeysDao)
        // Check if it's the instance from our AppDatabase
        assertEquals("RemoteKeysDao should be from the injected AppDatabase", appDatabase.remoteKeysDao(), remoteKeysDao)
    }
}
