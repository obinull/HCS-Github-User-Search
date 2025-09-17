package dev.byto.hcsgus.data.local.entity

import android.content.Context
import androidx.room.Room
import androidx.paging.PagingSource
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.dao.UserDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserEntityTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

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

    @Test
    fun userEntity_properties_assignedCorrectly_allFields() {
        val entity = UserEntity(
            id = 1,
            login = "testuser",
            avatarUrl = "http://example.com/avatar.png",
            gistsUrl = "gists_url",
            reposUrl = "repos_url",
            followingUrl = "following_url",
            starredUrl = "starred_url",
            followersUrl = "followers_url",
            type = "User",
            url = "url",
            subscriptionsUrl = "subscriptions_url",
            receivedEventsUrl = "received_events_url",
            eventsUrl = "events_url",
            htmlUrl = "html_url",
            siteAdmin = true,
            gravatarId = "gravatar_id",
            nodeId = "node_id",
            organizationsUrl = "organizations_url"
        )

        assertEquals(1, entity.id)
        assertEquals("testuser", entity.login)
        assertEquals("http://example.com/avatar.png", entity.avatarUrl)
        assertEquals("gists_url", entity.gistsUrl)
        assertEquals("repos_url", entity.reposUrl)
        assertEquals("following_url", entity.followingUrl)
        assertEquals("starred_url", entity.starredUrl)
        assertEquals("followers_url", entity.followersUrl)
        assertEquals("User", entity.type)
        assertEquals("url", entity.url)
        assertEquals("subscriptions_url", entity.subscriptionsUrl)
        assertEquals("received_events_url", entity.receivedEventsUrl)
        assertEquals("events_url", entity.eventsUrl)
        assertEquals("html_url", entity.htmlUrl)
        assertTrue(entity.siteAdmin ?: false)
        assertEquals("gravatar_id", entity.gravatarId)
        assertEquals("node_id", entity.nodeId)
        assertEquals("organizations_url", entity.organizationsUrl)
    }

    @Test
    fun userEntity_properties_assignedCorrectly_nullableFields() {
        // Primary key id defaults to 0, other vals must be provided or are nullable
        val entity = UserEntity(
            id = 2,
            login = "minimalUser",
            // All other fields are nullable and should default to null or their specified default if any
        )

        assertEquals(2, entity.id)
        assertEquals("minimalUser", entity.login)
        assertNull(entity.avatarUrl)
        assertNull(entity.gistsUrl)
        // For Boolean?, if not provided, it's null.
        // If your UserEntity has siteAdmin: Boolean? = null (which it does), then it should be null.
        assertNull(entity.siteAdmin)
    }

    @Test
    @Throws(Exception::class)
    fun userEntity_insertAndRetrieve_fromDatabase() = runTest {
        val originalEntity = UserEntity(
            id = 3,
            login = "dbUser",
            avatarUrl = "db_avatar.png",
            type = "Organization",
            siteAdmin = false
        )

        userDao.upsertAll(listOf(originalEntity))

        // Retrieve and verify. Using PagingSource requires a bit more setup to get a single item.
        // A direct query method in DAO like `getUserById(id: Int)` would be simpler for testing.
        // For this test, we load the first page and check if our item is there.
        val loadParams = PagingSource.LoadParams.Refresh<Int>(key = null, loadSize = 10, placeholdersEnabled = false)
        val pageResult = userDao.pagingSource().load(loadParams) as PagingSource.LoadResult.Page<Int, UserEntity>

        val retrievedEntity = pageResult.data.find { it.id == 3 }

        assertNotNull("Retrieved entity should not be null", retrievedEntity)
        assertEquals(originalEntity.id, retrievedEntity?.id)
        assertEquals(originalEntity.login, retrievedEntity?.login)
        assertEquals(originalEntity.avatarUrl, retrievedEntity?.avatarUrl)
        assertEquals(originalEntity.type, retrievedEntity?.type)
        assertEquals(originalEntity.siteAdmin, retrievedEntity?.siteAdmin)
    }
}
