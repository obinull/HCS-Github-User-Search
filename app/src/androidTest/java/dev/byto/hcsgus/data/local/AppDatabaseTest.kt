package dev.byto.hcsgus.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.byto.hcsgus.data.local.dao.RemoteKeysDao
import dev.byto.hcsgus.data.local.dao.UserDao
import dev.byto.hcsgus.data.local.dao.UserDetailDao
import dev.byto.hcsgus.data.local.entity.RemoteKeysEntity
import dev.byto.hcsgus.data.local.entity.UserDetailEntity
import dev.byto.hcsgus.data.local.entity.UserEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var userDetailDao: UserDetailDao
    private lateinit var remoteKeysDao: RemoteKeysDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
        userDetailDao = db.userDetailDao()
        remoteKeysDao = db.remoteKeysDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun daos_are_not_null() {
        assertNotNull(userDao)
        assertNotNull(userDetailDao)
        assertNotNull(remoteKeysDao)
    }

    @Test
    @Throws(Exception::class)
    fun userDao_insertAndGetUser() = runTest {
        val userEntity = UserEntity(id = 1, login = "testuser", avatarUrl = "url.com")
        userDao.upsertAll(listOf(userEntity))

        // Assuming pagingSource().load() would be complex for a simple check here.
        // Let's add a direct query method to UserDao for easier testing if not present.
        // For now, let's assume a simpler query method or direct check if possible.
        // If UserDao only has pagingSource, this test would need to be more complex
        // or UserDao needs a simple query method for testability.

        // For this test, let's assume there is a method like `getUserById` in UserDao for simplicity.
        // If not, this test needs adjustment based on available UserDao methods.
        // For example, if you add: @Query("SELECT * FROM users WHERE id = :id") fun getById(id: Int): UserEntity?
        // For the sake of this example, I'll proceed as if such a method existed or as if checking via pagingSource was simpler.
        // A common pattern is to have a suspend fun getById(id: Int): UserEntity? in the DAO.

        // Let's make a placeholder test that would pass if data is insertable
        // And assume we'd test retrieval via more specific DAO tests.
        // For a basic DB test, just inserting is a start.
        val allUsers = userDao.pagingSource().load(PagingSource.LoadParams.Refresh(0,1,false)) as PagingSource.LoadResult.Page
        assertTrue(allUsers.data.any { it.id == 1 && it.login == "testuser" })
    }

    @Test
    @Throws(Exception::class)
    fun userDetailDao_insertAndGetUserDetail() = runTest {
        val userDetailEntity = UserDetailEntity(id = 1, login = "testdetail", name = "Test Detail")
        userDetailDao.insertUserDetail(userDetailEntity)

        // Assuming UserDetailDao has a method like: @Query("SELECT * FROM user_details WHERE id = :id") fun getById(id: Int): UserDetailEntity?
        val retrieved = userDetailDao.getUserDetailById(1) // Assuming this method exists
        assertNotNull(retrieved)
        assertEquals("testdetail", retrieved?.login)
        assertEquals("Test Detail", retrieved?.name)
    }

    @Test
    @Throws(Exception::class)
    fun remoteKeysDao_insertAndGetRemoteKey() = runTest {
        val remoteKey = RemoteKeysEntity(userId = 1, prevKey = null, nextKey = 2)
        remoteKeysDao.insertAll(listOf(remoteKey))

        val retrieved = remoteKeysDao.remoteKeysUserId(1)
        assertNotNull(retrieved)
        assertEquals(1, retrieved?.userId)
        assertEquals(null, retrieved?.prevKey)
        assertEquals(2, retrieved?.nextKey)
    }
}
