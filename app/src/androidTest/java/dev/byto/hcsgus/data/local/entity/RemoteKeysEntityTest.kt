package dev.byto.hcsgus.data.local.entity

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.dao.RemoteKeysDao
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
class RemoteKeysEntityTest {

    private lateinit var db: AppDatabase
    private lateinit var remoteKeysDao: RemoteKeysDao

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
    fun remoteKeysEntity_properties_assignedCorrectly_allFields() {
        val userId = 1
        val prevKey = 10
        val nextKey = 12

        val entity = RemoteKeysEntity(
            userId = userId,
            prevKey = prevKey,
            nextKey = nextKey
        )

        assertEquals(userId, entity.userId)
        assertEquals(prevKey, entity.prevKey)
        assertEquals(nextKey, entity.nextKey)
    }

    @Test
    fun remoteKeysEntity_properties_assignedCorrectly_nullableFields() {
        val userId = 2

        // Test with prevKey null
        val entityWithNullPrev = RemoteKeysEntity(
            userId = userId,
            prevKey = null,
            nextKey = 3
        )
        assertEquals(userId, entityWithNullPrev.userId)
        assertNull(entityWithNullPrev.prevKey)
        assertEquals(3, entityWithNullPrev.nextKey)

        // Test with nextKey null
        val entityWithNullNext = RemoteKeysEntity(
            userId = userId,
            prevKey = 2,
            nextKey = null
        )
        assertEquals(userId, entityWithNullNext.userId)
        assertEquals(2, entityWithNullNext.prevKey)
        assertNull(entityWithNullNext.nextKey)

        // Test with both prevKey and nextKey null
        val entityWithBothNull = RemoteKeysEntity(
            userId = userId,
            prevKey = null,
            nextKey = null
        )
        assertEquals(userId, entityWithBothNull.userId)
        assertNull(entityWithBothNull.prevKey)
        assertNull(entityWithBothNull.nextKey)
    }

    @Test
    @Throws(Exception::class)
    fun remoteKeysEntity_insertAndRetrieve_fromDatabase() = runTest {
        val originalEntity = RemoteKeysEntity(
            userId = 100,
            prevKey = 99,
            nextKey = 101
        )

        // RemoteKeysDao.insertAll takes a list
        remoteKeysDao.insertAll(listOf(originalEntity))

        val retrievedEntity = remoteKeysDao.remoteKeysUserId(originalEntity.userId)

        assertNotNull("Retrieved entity should not be null", retrievedEntity)
        assertEquals(originalEntity.userId, retrievedEntity?.userId)
        assertEquals(originalEntity.prevKey, retrievedEntity?.prevKey)
        assertEquals(originalEntity.nextKey, retrievedEntity?.nextKey)
    }

    @Test
    @Throws(Exception::class)
    fun remoteKeysEntity_insertAndRetrieve_nullableKeys_fromDatabase() = runTest {
        val originalEntity = RemoteKeysEntity(
            userId = 101,
            prevKey = null,
            nextKey = 102
        )

        remoteKeysDao.insertAll(listOf(originalEntity))
        val retrievedEntity = remoteKeysDao.remoteKeysUserId(originalEntity.userId)
        assertNotNull(retrievedEntity)
        assertEquals(originalEntity.userId, retrievedEntity?.userId)
        assertNull(retrievedEntity?.prevKey)
        assertEquals(originalEntity.nextKey, retrievedEntity?.nextKey)

        val originalEntity2 = RemoteKeysEntity(
            userId = 103,
            prevKey = 102,
            nextKey = null
        )
        remoteKeysDao.insertAll(listOf(originalEntity2))
        val retrievedEntity2 = remoteKeysDao.remoteKeysUserId(originalEntity2.userId)
        assertNotNull(retrievedEntity2)
        assertEquals(originalEntity2.userId, retrievedEntity2?.userId)
        assertEquals(originalEntity2.prevKey, retrievedEntity2?.prevKey)
        assertNull(retrievedEntity2?.nextKey)
    }
}
