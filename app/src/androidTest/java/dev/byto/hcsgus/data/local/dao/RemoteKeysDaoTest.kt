package dev.byto.hcsgus.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.entity.RemoteKeysEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemoteKeysDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var remoteKeysDao: RemoteKeysDao

    private val key1 = RemoteKeysEntity(userId = 1, prevKey = null, nextKey = 2)
    private val key2 = RemoteKeysEntity(userId = 2, prevKey = 1, nextKey = 3)
    private val key3 = RemoteKeysEntity(userId = 3, prevKey = 2, nextKey = null)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // For testing simplicity
            .build()
        remoteKeysDao = db.remoteKeysDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAll_and_remoteKeysUserId_singleKey() = runTest {
        remoteKeysDao.insertAll(listOf(key1))

        val retrievedKey = remoteKeysDao.remoteKeysUserId(key1.userId)
        assertNotNull(retrievedKey)
        assertEquals(key1.userId, retrievedKey?.userId)
        assertEquals(key1.prevKey, retrievedKey?.prevKey)
        assertEquals(key1.nextKey, retrievedKey?.nextKey)
    }

    @Test
    @Throws(Exception::class)
    fun insertAll_and_remoteKeysUserId_multipleKeys() = runTest {
        val keysToInsert = listOf(key1, key2, key3)
        remoteKeysDao.insertAll(keysToInsert)

        val retrievedKey1 = remoteKeysDao.remoteKeysUserId(key1.userId)
        assertEquals(key1, retrievedKey1)

        val retrievedKey2 = remoteKeysDao.remoteKeysUserId(key2.userId)
        assertEquals(key2, retrievedKey2)

        val retrievedKey3 = remoteKeysDao.remoteKeysUserId(key3.userId)
        assertEquals(key3, retrievedKey3)
    }

    @Test
    @Throws(Exception::class)
    fun insertAll_withConflict_replacesExistingKey() = runTest {
        remoteKeysDao.insertAll(listOf(key1)) // Initial insert for userId = 1

        val updatedKey1 = RemoteKeysEntity(userId = 1, prevKey = 100, nextKey = 102) // Same userId, different keys
        remoteKeysDao.insertAll(listOf(updatedKey1)) // Upsert with updated data

        val retrievedKey = remoteKeysDao.remoteKeysUserId(key1.userId)
        assertNotNull(retrievedKey)
        assertEquals(updatedKey1.userId, retrievedKey?.userId)
        assertEquals(updatedKey1.prevKey, retrievedKey?.prevKey)
        assertEquals(updatedKey1.nextKey, retrievedKey?.nextKey)
    }

    @Test
    @Throws(Exception::class)
    fun remoteKeysUserId_returnsNull_forNonExistentKey() = runTest {
        val retrievedKey = remoteKeysDao.remoteKeysUserId(999) // userId 999 does not exist
        assertNull(retrievedKey)
    }

    @Test
    @Throws(Exception::class)
    fun clearRemoteKeys_deletesAllKeys() = runTest {
        val keysToInsert = listOf(key1, key2, key3)
        remoteKeysDao.insertAll(keysToInsert)

        // Verify keys were inserted
        assertNotNull(remoteKeysDao.remoteKeysUserId(key1.userId))
        assertNotNull(remoteKeysDao.remoteKeysUserId(key2.userId))
        assertNotNull(remoteKeysDao.remoteKeysUserId(key3.userId))

        remoteKeysDao.clearRemoteKeys()

        // Verify keys are now deleted
        assertNull(remoteKeysDao.remoteKeysUserId(key1.userId))
        assertNull(remoteKeysDao.remoteKeysUserId(key2.userId))
        assertNull(remoteKeysDao.remoteKeysUserId(key3.userId))
    }
}
