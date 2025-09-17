package dev.byto.hcsgus.data.remote.dto

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UserDetailDtoTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(UserDetailDto::class.java)

    private fun createSampleUserDetailDto(): UserDetailDto {
        return UserDetailDto(
            gistsUrl = "gists_url_val",
            reposUrl = "repos_url_val",
            followingUrl = "following_url_val",
            twitterUsername = "twitter_user",
            bio = "This is a bio.",
            createdAt = "2023-01-01T00:00:00Z",
            login = "testuser",
            type = "User",
            blog = "example.com/blog",
            subscriptionsUrl = "subscriptions_url_val",
            updatedAt = "2023-01-02T00:00:00Z",
            siteAdmin = false,
            company = "Test Company",
            id = 123,
            publicRepos = 10,
            gravatarId = "gravatar_id_val",
            email = "test@example.com",
            organizationsUrl = "organizations_url_val",
            hireable = true,
            starredUrl = "starred_url_val",
            followersUrl = "followers_url_val",
            publicGists = 5,
            url = "url_val",
            receivedEventsUrl = "received_events_url_val",
            followers = 100,
            avatarUrl = "avatar_url_val",
            eventsUrl = "events_url_val",
            htmlUrl = "html_url_val",
            following = 50,
            name = "Test User Name",
            location = "Test Location",
            nodeId = "node_id_val"
        )
    }

    @Test
    fun `userDetailDto properties are assigned correctly`() {
        val dto = createSampleUserDetailDto()

        assertEquals("gists_url_val", dto.gistsUrl)
        assertEquals("repos_url_val", dto.reposUrl)
        assertEquals("following_url_val", dto.followingUrl)
        assertEquals("twitter_user", dto.twitterUsername)
        assertEquals("This is a bio.", dto.bio)
        assertEquals("2023-01-01T00:00:00Z", dto.createdAt)
        assertEquals("testuser", dto.login)
        assertEquals("User", dto.type)
        assertEquals("example.com/blog", dto.blog)
        assertEquals("subscriptions_url_val", dto.subscriptionsUrl)
        assertEquals("2023-01-02T00:00:00Z", dto.updatedAt)
        assertEquals(false, dto.siteAdmin)
        assertEquals("Test Company", dto.company)
        assertEquals(123, dto.id)
        assertEquals(10, dto.publicRepos)
        assertEquals("gravatar_id_val", dto.gravatarId)
        assertEquals("test@example.com", dto.email)
        assertEquals("organizations_url_val", dto.organizationsUrl)
        assertEquals(true, dto.hireable)
        assertEquals("starred_url_val", dto.starredUrl)
        assertEquals("followers_url_val", dto.followersUrl)
        assertEquals(5, dto.publicGists)
        assertEquals("url_val", dto.url)
        assertEquals("received_events_url_val", dto.receivedEventsUrl)
        assertEquals(100, dto.followers)
        assertEquals("avatar_url_val", dto.avatarUrl)
        assertEquals("events_url_val", dto.eventsUrl)
        assertEquals("html_url_val", dto.htmlUrl)
        assertEquals(50, dto.following)
        assertEquals("Test User Name", dto.name)
        assertEquals("Test Location", dto.location)
        assertEquals("node_id_val", dto.nodeId)
    }

    @Test
    fun `userDetailDto with null properties assigned correctly`() {
        val dto = UserDetailDto(id = 1, login = "minimal")

        assertEquals(1, dto.id)
        assertEquals("minimal", dto.login)
        assertNull(dto.gistsUrl) // Example of a nullable field
        assertNull(dto.twitterUsername)
        assertNull(dto.bio)
        // ... assert other nullable fields are null
    }

    @Test
    fun `userDetailDto Moshi serialization and deserialization - all fields`() {
        val originalDto = createSampleUserDetailDto()

        val jsonString = adapter.toJson(originalDto)
        assertNotNull(jsonString)

        val deserializedDto = adapter.fromJson(jsonString)
        assertNotNull(deserializedDto)
        assertEquals(originalDto, deserializedDto)
    }

    @Test
    fun `userDetailDto Moshi deserialization - some nullable fields missing`() {
        val jsonString = """{
            "login": "partial_user",
            "id": 456,
            "name": "Partial Name",
            "public_repos": 5,
            "followers": 20
        }"""

        val deserializedDto = adapter.fromJson(jsonString)
        assertNotNull(deserializedDto)
        assertEquals("partial_user", deserializedDto?.login)
        assertEquals(456, deserializedDto?.id)
        assertEquals("Partial Name", deserializedDto?.name)
        assertEquals(5, deserializedDto?.publicRepos)
        assertEquals(20, deserializedDto?.followers)

        // Assert that fields not in JSON are null
        assertNull(deserializedDto?.gistsUrl)
        assertNull(deserializedDto?.twitterUsername)
        assertNull(deserializedDto?.bio)
        assertNull(deserializedDto?.createdAt)
        assertNull(deserializedDto?.type)
        assertNull(deserializedDto?.blog)
        // ... and so on for all other nullable fields
        assertNull(deserializedDto?.company)
        assertNull(deserializedDto?.location)
        assertNull(deserializedDto?.email)
    }
}
