package dev.byto.hcsgus.data.local.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.entity.UserEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    private val user1 = UserEntity(
        id = 1,
        login = "userone",
        avatarUrl = "https://example.com/avatars/userone.png",
        gistsUrl = "https://api.github.com/users/userone/gists{/gist_id}",
        reposUrl = "https://api.github.com/users/userone/repos",
        followingUrl = "https://api.github.com/users/userone/following{/other_user}",
        starredUrl = "https://api.github.com/users/userone/starred{/owner}{/repo}",
        followersUrl = "https://api.github.com/users/userone/followers",
        type = "User",
        url = "https://api.github.com/users/userone",
        subscriptionsUrl = "https://api.github.com/users/userone/subscriptions",
        receivedEventsUrl = "https://api.github.com/users/userone/received_events",
        eventsUrl = "https://api.github.com/users/userone/events{/privacy}",
        htmlUrl = "https://github.com/userone",
        siteAdmin = false,
        gravatarId = "1abcde12345fghij67890klmno",
        nodeId = "MDQ6VXNlcjE=", // Base64 encoded "User:1"
        organizationsUrl = "https://api.github.com/users/userone/orgs"
    )

    private val user2 = UserEntity(
        id = 2,
        login = "usertwo",
        avatarUrl = "https://example.com/avatars/usertwo.jpg",
        gistsUrl = "https://api.github.com/users/usertwo/gists{/gist_id}",
        reposUrl = "https://api.github.com/users/usertwo/repos",
        followingUrl = "https://api.github.com/users/usertwo/following{/other_user}",
        starredUrl = "https://api.github.com/users/usertwo/starred{/owner}{/repo}",
        followersUrl = "https://api.github.com/users/usertwo/followers",
        type = "User",
        url = "https://api.github.com/users/usertwo",
        subscriptionsUrl = "https://api.github.com/users/usertwo/subscriptions",
        receivedEventsUrl = "https://api.github.com/users/usertwo/received_events",
        eventsUrl = "https://api.github.com/users/usertwo/events{/privacy}",
        htmlUrl = "https://github.com/usertwo",
        siteAdmin = false,
        gravatarId = "2bcde12345fghij67890klmno1",
        nodeId = "MDQ6VXNlcjI=", // Base64 encoded "User:2"
        organizationsUrl = "https://api.github.com/users/usertwo/orgs"
    )

    private val user3 = UserEntity(
        id = 3,
        login = "userthree",
        avatarUrl = "https://example.com/avatars/userthree.gif",
        gistsUrl = "https://api.github.com/users/userthree/gists{/gist_id}",
        reposUrl = "https://api.github.com/users/userthree/repos",
        followingUrl = "https://api.github.com/users/userthree/following{/other_user}",
        starredUrl = "https://api.github.com/users/userthree/starred{/owner}{/repo}",
        followersUrl = "https://api.github.com/users/userthree/followers",
        type = "Organization", // Changed type for variety
        url = "https://api.github.com/users/userthree",
        subscriptionsUrl = "https://api.github.com/users/userthree/subscriptions",
        receivedEventsUrl = "https://api.github.com/users/userthree/received_events",
        eventsUrl = "https://api.github.com/users/userthree/events{/privacy}",
        htmlUrl = "https://github.com/userthree",
        siteAdmin = true, // Changed for variety
        gravatarId = "", // Can be empty
        nodeId = "MDQ6VXNlcjM=", // Base64 encoded "User:3" (or "Organization:3")
        organizationsUrl = "https://api.github.com/users/userthree/orgs"
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // For testing simplicity
            .build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private suspend fun loadAllUsersFromPagingSource(): List<UserEntity> {
        val pagingSource = userDao.pagingSource()
        val params = PagingSource.LoadParams.Refresh<Int>(
            key = null, // Load from the beginning
            loadSize = 100, // Large enough to load all test data
            placeholdersEnabled = false
        )
        val pageResult = pagingSource.load(params) as PagingSource.LoadResult.Page<Int, UserEntity>
        return pageResult.data
    }

    @Test
    @Throws(Exception::class)
    fun upsertAll_insertsNewUsers() = runTest {
        val usersToInsert = listOf(user1, user2)
        userDao.upsertAll(usersToInsert)

        val loadedUsers = loadAllUsersFromPagingSource()
        assertEquals(2, loadedUsers.size)
        assertTrue(loadedUsers.contains(user1))
        assertTrue(loadedUsers.contains(user2))
    }

    @Test
    @Throws(Exception::class)
    fun upsertAll_updatesExistingUsers() = runTest {
        userDao.upsertAll(listOf(user1)) // Initial insert

        val updatedUser1 = UserEntity(id = 1, login = "userone_updated", avatarUrl = "url1_updated")
        userDao.upsertAll(listOf(updatedUser1)) // Upsert with updated data

        val loadedUsers = loadAllUsersFromPagingSource()
        assertEquals(1, loadedUsers.size)
        assertEquals("userone_updated", loadedUsers[0].login)
        assertEquals("url1_updated", loadedUsers[0].avatarUrl)
    }

    @Test
    @Throws(Exception::class)
    fun pagingSource_returnsEmptyList_whenTableIsEmpty() = runTest {
        val loadedUsers = loadAllUsersFromPagingSource()
        assertTrue(loadedUsers.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun pagingSource_returnsData_orderedByIdAsc() = runTest {
        // Insert in non-sequential order of ID for testing ordering
        val usersToInsert = listOf(user3, user1, user2)
        userDao.upsertAll(usersToInsert)

        val loadedUsers = loadAllUsersFromPagingSource()
        assertEquals(3, loadedUsers.size)
        assertEquals(user1.id, loadedUsers[0].id)
        assertEquals(user2.id, loadedUsers[1].id)
        assertEquals(user3.id, loadedUsers[2].id)
    }

    @Test
    @Throws(Exception::class)
    fun clearAll_deletesAllUsers() = runTest {
        val usersToInsert = listOf(user1, user2, user3)
        userDao.upsertAll(usersToInsert)

        var loadedUsers = loadAllUsersFromPagingSource()
        assertEquals(3, loadedUsers.size) // Verify users were inserted

        userDao.clearAll()
        loadedUsers = loadAllUsersFromPagingSource()
        assertTrue(loadedUsers.isEmpty())
    }
}
