package dev.byto.hcsgus.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.entity.UserDetailEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserDetailDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var userDetailDao: UserDetailDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Use in-memory database for testing
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Allow queries on main thread for testing simplicity
            .build()
        userDetailDao = db.userDetailDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    rivate fun sampleUserDetailEntity(
        id: Int,
        login: String,
        name: String? = "Sample Name $login", // Made name more dynamic
        bioText: String? = "A detailed bio for $login describing their passions and projects."
    ) = UserDetailEntity(
        id = id,
        login = login,
        name = name,
        avatarUrl = "https://example.com/avatar/$login.png",
        bio = bioText,
        company = "Tech Solutions Ltd. for $login",
        location = "Cityville, State of $login",
        followers = (id * 100) + 50, // Made dynamic based on id
        following = (id * 50) + 20,  // Made dynamic based on id
        publicRepos = (id * 10) + 5, // Made dynamic based on id
        gistsUrl = "https://api.github.com/users/$login/gists{/gist_id}",
        reposUrl = "https://api.github.com/users/$login/repos",
        followingUrl = "https://api.github.com/users/$login/following{/other_user}",
        twitterUsername = if (id % 2 == 0) "@${login}_twitter" else null, // Conditional twitter
        createdAt = "202${id % 10}-0${(id % 9) + 1}-0${(id % 20) + 1}T10:00:00Z", // Dynamic date
        type = if (id % 3 == 0) "Organization" else "User", // Varied type
        blog = if (id % 2 != 0) "https://blog.$login.example.com" else null,
        subscriptionsUrl = "https://api.github.com/users/$login/subscriptions",
        updatedAt = "202${id % 10}-0${(id % 9) + 2}-0${(id % 20) + 5}T12:30:00Z", // Dynamic updated date
        siteAdmin = (id % 4 == 0), // Varied siteAdmin
        publicGists = (id * 2) + 1,
        gravatarId = "${id}abcdef123456hij7890klmno",
        email = if (id % 2 == 0) "$login@example.com" else null,
        organizationsUrl = "https://api.github.com/users/$login/orgs",
        hireable = if (id % 3 != 0) true else null, // Varied hireable
        starredUrl = "https://api.github.com/users/$login/starred{/owner}{/repo}",
        followersUrl = "https://api.github.com/users/$login/followers",
        url = "https://api.github.com/users/$login",
        receivedEventsUrl = "https://api.github.com/users/$login/received_events",
        eventsUrl = "https://api.github.com/users/$login/events{/privacy}",
        htmlUrl = "https://github.com/$login",
        nodeId = "MDQ6VXNlcj${id + 1000}" // Dynamic nodeId
    )

    @Test
    @Throws(Exception::class)
    fun insertUserDetail_insertsSuccessfully() = runTest {
        val userId = 1
        val userLogin = "testuser1"
        val userName = "First User"
        // The bio will come from the default bioText in the updated sampleUserDetailEntity
        val expectedBio = "A detailed bio for $userLogin describing their passions and projects."

        val userDetail = sampleUserDetailEntity(id = userId, login = userLogin, name = userName)
        userDetailDao.insertUserDetail(userDetail)

        val retrievedUserDetail = userDetailDao.getUserDetail(userLogin).first()

        assertNotNull(retrievedUserDetail)
        assertEquals(userDetail.id, retrievedUserDetail?.id)
        assertEquals(userDetail.login, retrievedUserDetail?.login)
        assertEquals(userName, retrievedUserDetail?.name) // Asserting the passed name
        assertEquals(expectedBio, retrievedUserDetail?.bio) // Asserting the expected bio

        // Add assertions for other fields populated by sampleUserDetailEntity:
        assertEquals("https://example.com/avatar/$userLogin.png", retrievedUserDetail?.avatarUrl)
        assertEquals("Tech Solutions Ltd. for $userLogin", retrievedUserDetail?.company)
        assertEquals("Cityville, State of $userLogin", retrievedUserDetail?.location)
        assertEquals((userId * 100) + 50, retrievedUserDetail?.followers)
        assertEquals((userId * 50) + 20, retrievedUserDetail?.following)
        assertEquals((userId * 10) + 5, retrievedUserDetail?.publicRepos)

        // Example for a conditionally set field based on id=1
        assertEquals("https://blog.$userLogin.example.com", retrievedUserDetail?.blog) // id=1, so blog is not null
        Assert.assertNull(retrievedUserDetail?.twitterUsername) // id=1, so twitterUsername is null
        assertEquals(false, retrievedUserDetail?.siteAdmin) // id=1, 1%4 != 0
        assertEquals(true, retrievedUserDetail?.hireable) // id=1, 1%3 != 0

    }

    @Test
    @Throws(Exception::class)
    fun insertUserDetail_withConflict_replacesExisting() = runTest {
        val initialLogin = "conflictUser"
        val initialId = 1
        val initialUserDetail = sampleUserDetailEntity(initialId, initialLogin, "Initial Name")
        userDetailDao.insertUserDetail(initialUserDetail)

        // Verify initial insert
        var retrieved = userDetailDao.getUserDetail(initialLogin).first()
        assertEquals("Initial Name", retrieved?.name)
        assertEquals("A detailed bio for $initialLogin describing their passions and projects.", retrieved?.bio) // From sampleUserDetailEntity default
        assertEquals((initialId * 100) + 50, retrieved?.followers) // From sampleUserDetailEntity default

        val updatedUserDetail = UserDetailEntity(
            id = initialId, // Same ID for conflict
            login = initialLogin, // Same login for query purpose
            name = "Updated Name", // Different name
            avatarUrl = initialUserDetail.avatarUrl, // Keep from initial or update if desired
            bio = "Updated bio for conflictUser reflecting new changes.", // Different bio
            company = "Updated Company Inc.", // Different company
            location = "New Location City", // Different location
            followers = 2000,      // Different followers
            following = initialUserDetail.following?.plus(10),   // Modified from initial
            publicRepos = initialUserDetail.publicRepos?.plus(5), // Modified from initial
            // --- Fill in other UserDetailEntity fields ---
            gistsUrl = "https://api.github.com/users/$initialLogin/gists{/gist_id}/updated",
            reposUrl = "https://api.github.com/users/$initialLogin/repos/updated",
            followingUrl = "https://api.github.com/users/$initialLogin/following{/other_user}/updated",
            twitterUsername = "@${initialLogin}_updated",
            createdAt = initialUserDetail.createdAt, // Or update if needed: "2023-03-15T10:00:00Z",
            type = "User", // Can be same or different
            blog = "https://blog.$initialLogin.updated.example.com",
            subscriptionsUrl = "https://api.github.com/users/$initialLogin/subscriptions/updated",
            updatedAt = "2023-03-16T18:30:00Z", // Explicitly new updated_at
            siteAdmin = !(initialUserDetail.siteAdmin ?: false), // Flipped from initial
            publicGists = initialUserDetail.publicGists?.plus(3),
            gravatarId = initialUserDetail.gravatarId + "_updated",
            email = "$initialLogin.updated@example.com",
            organizationsUrl = "https://api.github.com/users/$initialLogin/orgs/updated",
            hireable = !(initialUserDetail.hireable ?: false), // Flipped from initial
            starredUrl = "https://api.github.com/users/$initialLogin/starred{/owner}{/repo}/updated",
            followersUrl = "https://api.github.com/users/$initialLogin/followers/updated",
            url = "https://api.github.com/users/$initialLogin/updated",
            receivedEventsUrl = "https://api.github.com/users/$initialLogin/received_events/updated",
            eventsUrl = "https://api.github.com/users/$initialLogin/events{/privacy}/updated",
            htmlUrl = "https://github.com/$initialLogin/updated",
            nodeId = initialUserDetail.nodeId + "updated"
        )
        userDetailDao.insertUserDetail(updatedUserDetail)

        retrieved = userDetailDao.getUserDetail(initialLogin).first()
        assertNotNull(retrieved)
        Assert.assertEquals(initialId, retrieved?.id)
        Assert.assertEquals(initialLogin, retrieved?.login)
        // Assert the updated fields
        assertEquals("Updated Name", retrieved?.name)
        assertEquals("Updated bio for conflictUser reflecting new changes.", retrieved?.bio)
        assertEquals(2000, retrieved?.followers)
        assertEquals("Updated Company Inc.", retrieved?.company)
        assertEquals("New Location City", retrieved?.location)
        assertEquals(initialUserDetail.following?.plus(10), retrieved?.following)
        assertEquals(initialUserDetail.publicRepos?.plus(5), retrieved?.publicRepos)
        assertEquals("@${initialLogin}_updated", retrieved?.twitterUsername)
    }
}
