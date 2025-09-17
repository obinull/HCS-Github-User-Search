package dev.byto.hcsgus.data.local.entity

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.dao.UserDetailDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserDetailEntityTest {

    private lateinit var db: AppDatabase
    private lateinit var userDetailDao: UserDetailDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // For testing simplicity
            .build()
        userDetailDao = db.userDetailDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun createFullUserDetailEntity(): UserDetailEntity {
        return UserDetailEntity(
            id = 1,
            login = "testuser",
            avatarUrl = "http://example.com/avatar.png",
            gistsUrl = "gists_url",
            reposUrl = "repos_url",
            followingUrl = "following_url",
            twitterUsername = "twitter_handle",
            bio = "This is a test bio.",
            createdAt = "2023-01-01T10:00:00Z",
            type = "User",
            blog = "http://example.com/blog",
            subscriptionsUrl = "subscriptions_url",
            updatedAt = "2023-01-02T12:00:00Z",
            siteAdmin = false,
            company = "Test Company Inc.",
            publicRepos = 10,
            gravatarId = "gravatar_id_value",
            email = "testuser@example.com",
            organizationsUrl = "organizations_url",
            hireable = true,
            starredUrl = "starred_url",
            followersUrl = "followers_url",
            publicGists = 5,
            url = "http://example.com/user",
            receivedEventsUrl = "received_events_url",
            followers = 100,
            eventsUrl = "events_url",
            htmlUrl = "html_url",
            following = 50,
            name = "Test User Name",
            location = "Test Location, USA",
            nodeId = "node_id_value"
        )
    }

    @Test
    fun userDetailEntity_properties_assignedCorrectly_allFields() {
        val entity = createFullUserDetailEntity()

        assertEquals(1, entity.id)
        assertEquals("testuser", entity.login)
        assertEquals("http://example.com/avatar.png", entity.avatarUrl)
        assertEquals("gists_url", entity.gistsUrl)
        assertEquals("repos_url", entity.reposUrl)
        assertEquals("following_url", entity.followingUrl)
        assertEquals("twitter_handle", entity.twitterUsername)
        assertEquals("This is a test bio.", entity.bio)
        assertEquals("2023-01-01T10:00:00Z", entity.createdAt)
        assertEquals("User", entity.type)
        assertEquals("http://example.com/blog", entity.blog)
        assertEquals("subscriptions_url", entity.subscriptionsUrl)
        assertEquals("2023-01-02T12:00:00Z", entity.updatedAt)
        assertEquals(false, entity.siteAdmin)
        assertEquals("Test Company Inc.", entity.company)
        assertEquals(10, entity.publicRepos)
        assertEquals("gravatar_id_value", entity.gravatarId)
        assertEquals("testuser@example.com", entity.email)
        assertEquals("organizations_url", entity.organizationsUrl)
        assertEquals(true, entity.hireable)
        assertEquals("starred_url", entity.starredUrl)
        assertEquals("followers_url", entity.followersUrl)
        assertEquals(5, entity.publicGists)
        assertEquals("http://example.com/user", entity.url)
        assertEquals("received_events_url", entity.receivedEventsUrl)
        assertEquals(100, entity.followers)
        assertEquals("events_url", entity.eventsUrl)
        assertEquals("html_url", entity.htmlUrl)
        assertEquals(50, entity.following)
        assertEquals("Test User Name", entity.name)
        assertEquals("Test Location, USA", entity.location)
        assertEquals("node_id_value", entity.nodeId)
    }

    @Test
    fun userDetailEntity_properties_assignedCorrectly_minimalFields() {
        val entity = UserDetailEntity(
            id = 2,
            login = "minimalUser"
            // All other fields are nullable
        )

        assertEquals(2, entity.id)
        assertEquals("minimalUser", entity.login)
        assertNull(entity.avatarUrl)
        assertNull(entity.gistsUrl)
        assertNull(entity.twitterUsername)
        assertNull(entity.bio)
        // ... assert other nullable fields are null
        assertNull(entity.company)
        assertNull(entity.publicRepos) // Int? defaults to null
        assertNull(entity.email)
        assertNull(entity.hireable) // Boolean? defaults to null
        assertNull(entity.name)
        assertNull(entity.location)
    }

    @Test
    @Throws(Exception::class)
    fun userDetailEntity_insertAndRetrieve_fromDatabase() = runTest {
        val originalEntity = createFullUserDetailEntity()

        userDetailDao.insertUserDetail(originalEntity)

        // Retrieve using the existing DAO method that takes username (login)
        val retrievedEntityFlow = userDetailDao.getUserDetail(originalEntity.login!!)
        val retrievedEntity = retrievedEntityFlow.first() // Collect the first emitted value

        assertNotNull("Retrieved entity should not be null", retrievedEntity)
        assertEquals(originalEntity.id, retrievedEntity?.id)
        assertEquals(originalEntity.login, retrievedEntity?.login)
        assertEquals(originalEntity.avatarUrl, retrievedEntity?.avatarUrl)
        assertEquals(originalEntity.name, retrievedEntity?.name)
        assertEquals(originalEntity.company, retrievedEntity?.company)
        assertEquals(originalEntity.location, retrievedEntity?.location)
        assertEquals(originalEntity.bio, retrievedEntity?.bio)
        assertEquals(originalEntity.publicRepos, retrievedEntity?.publicRepos)
        assertEquals(originalEntity.followers, retrievedEntity?.followers)
        assertEquals(originalEntity.following, retrievedEntity?.following)
        // ... can add more assertions for other fields if needed
    }
}
