package dev.byto.hcsgus.data.remote.dto

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UserDtoTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(UserDto::class.java)

    @Test
    fun `userDto properties are assigned correctly`() {
        val gistsUrl = "gists_url_val"
        val reposUrl = "repos_url_val"
        val followingUrl = "following_url_val"
        val starredUrl = "starred_url_val"
        val login = "login_val"
        val followersUrl = "followers_url_val"
        val type = "User"
        val url = "url_val"
        val subscriptionsUrl = "subscriptions_url_val"
        val receivedEventsUrl = "received_events_url_val"
        val avatarUrl = "avatar_url_val"
        val eventsUrl = "events_url_val"
        val htmlUrl = "html_url_val"
        val siteAdmin = false
        val id = 123
        val gravatarId = "gravatar_id_val"
        val nodeId = "node_id_val"
        val organizationsUrl = "organizations_url_val"
        val userViewType = "list_item_view_type"
        val score = 100

        val userDto = UserDto(
            gistsUrl = gistsUrl,
            reposUrl = reposUrl,
            followingUrl = followingUrl,
            starredUrl = starredUrl,
            login = login,
            followersUrl = followersUrl,
            type = type,
            url = url,
            subscriptionsUrl = subscriptionsUrl,
            receivedEventsUrl = receivedEventsUrl,
            avatarUrl = avatarUrl,
            eventsUrl = eventsUrl,
            htmlUrl = htmlUrl,
            siteAdmin = siteAdmin,
            id = id,
            gravatarId = gravatarId,
            nodeId = nodeId,
            organizationsUrl = organizationsUrl,
            userViewType = userViewType,
            score = score
        )

        assertEquals(gistsUrl, userDto.gistsUrl)
        assertEquals(reposUrl, userDto.reposUrl)
        assertEquals(followingUrl, userDto.followingUrl)
        assertEquals(starredUrl, userDto.starredUrl)
        assertEquals(login, userDto.login)
        assertEquals(followersUrl, userDto.followersUrl)
        assertEquals(type, userDto.type)
        assertEquals(url, userDto.url)
        assertEquals(subscriptionsUrl, userDto.subscriptionsUrl)
        assertEquals(receivedEventsUrl, userDto.receivedEventsUrl)
        assertEquals(avatarUrl, userDto.avatarUrl)
        assertEquals(eventsUrl, userDto.eventsUrl)
        assertEquals(htmlUrl, userDto.htmlUrl)
        assertEquals(siteAdmin, userDto.siteAdmin)
        assertEquals(id, userDto.id)
        assertEquals(gravatarId, userDto.gravatarId)
        assertEquals(nodeId, userDto.nodeId)
        assertEquals(organizationsUrl, userDto.organizationsUrl)
        assertEquals(userViewType, userDto.userViewType)
        assertEquals(score, userDto.score)
    }

    @Test
    fun `userDto with null properties assigned correctly`() {
        val userDto = UserDto(
            id = 1, login = "test"
            // All other properties are null by default or explicitly
        )
        assertEquals(1, userDto.id)
        assertEquals("test", userDto.login)
        assertNull(userDto.gistsUrl)
        assertNull(userDto.avatarUrl)
        // ... assert other nullable fields are null
        assertNull(userDto.score)
    }

    @Test
    fun `userDto Moshi serialization and deserialization - all fields`() {
        val originalDto = UserDto(
            gistsUrl = "gists_url_val",
            reposUrl = "repos_url_val",
            followingUrl = "following_url_val",
            starredUrl = "starred_url_val",
            login = "login_val",
            followersUrl = "followers_url_val",
            type = "User",
            url = "url_val",
            subscriptionsUrl = "subscriptions_url_val",
            receivedEventsUrl = "received_events_url_val",
            avatarUrl = "avatar_url_val",
            eventsUrl = "events_url_val",
            htmlUrl = "html_url_val",
            siteAdmin = false,
            id = 123,
            gravatarId = "gravatar_id_val",
            nodeId = "node_id_val",
            organizationsUrl = "organizations_url_val",
            userViewType = "list_item_view_type",
            score = 100
        )

        val jsonString = adapter.toJson(originalDto)
        assertNotNull(jsonString)

        val deserializedDto = adapter.fromJson(jsonString)
        assertNotNull(deserializedDto)
        assertEquals(originalDto, deserializedDto)
    }

    @Test
    fun `userDto Moshi deserialization - some nullable fields missing from JSON`() {
        val jsonString = """{
            "login": "test_login",
            "id": 789,
            "avatar_url": "test_avatar_url",
            "type": "Organization"
        }"""

        val deserializedDto = adapter.fromJson(jsonString)
        assertNotNull(deserializedDto)
        assertEquals("test_login", deserializedDto?.login)
        assertEquals(789, deserializedDto?.id)
        assertEquals("test_avatar_url", deserializedDto?.avatarUrl)
        assertEquals("Organization", deserializedDto?.type)

        // Assert that fields not in JSON are null
        assertNull(deserializedDto?.gistsUrl)
        assertNull(deserializedDto?.reposUrl)
        assertNull(deserializedDto?.followingUrl)
        assertNull(deserializedDto?.starredUrl)
        assertNull(deserializedDto?.followersUrl)
        assertNull(deserializedDto?.url)
        assertNull(deserializedDto?.subscriptionsUrl)
        assertNull(deserializedDto?.receivedEventsUrl)
        assertNull(deserializedDto?.eventsUrl)
        assertNull(deserializedDto?.htmlUrl)
        assertNull(deserializedDto?.siteAdmin)
        assertNull(deserializedDto?.gravatarId)
        assertNull(deserializedDto?.nodeId)
        assertNull(deserializedDto?.organizationsUrl)
        assertNull(deserializedDto?.userViewType)
        assertNull(deserializedDto?.score)
    }
}
