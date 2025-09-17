package dev.byto.hcsgus.data.remote.dto

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchResponseDtoTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(SearchResponseDto::class.java)

    private fun createSampleUserDto(id: Int, login: String): UserDto {
        return UserDto(id = id, login = login, avatarUrl = "avatar_url_$id")
    }

    @Test
    fun `searchResponseDto properties are assigned correctly`() {
        val user1 = createSampleUserDto(1, "user1")
        val user2 = createSampleUserDto(2, "user2")
        val items = listOf(user1, user2)
        val totalCount = 150
        val incompleteResults = false

        val dto = SearchResponseDto(
            totalCount = totalCount,
            incompleteResults = incompleteResults,
            items = items
        )

        assertEquals(totalCount, dto.totalCount)
        assertEquals(incompleteResults, dto.incompleteResults)
        assertEquals(items, dto.items)
        assertEquals(2, dto.items.size)
        assertEquals(user1, dto.items[0])
    }

    @Test
    fun `searchResponseDto with empty items assigned correctly`() {
        val items = emptyList<UserDto>()
        val totalCount = 0
        val incompleteResults = true

        val dto = SearchResponseDto(
            totalCount = totalCount,
            incompleteResults = incompleteResults,
            items = items
        )

        assertEquals(totalCount, dto.totalCount)
        assertEquals(incompleteResults, dto.incompleteResults)
        assertTrue(dto.items.isEmpty())
    }

    @Test
    fun `searchResponseDto Moshi serialization and deserialization - with items`() {
        val originalDto = SearchResponseDto(
            totalCount = 2,
            incompleteResults = false,
            items = listOf(createSampleUserDto(10, "login10"), createSampleUserDto(20, "login20"))
        )

        val jsonString = adapter.toJson(originalDto)
        assertNotNull(jsonString)

        val deserializedDto = adapter.fromJson(jsonString)
        assertNotNull(deserializedDto)
        assertEquals(originalDto, deserializedDto)
    }

    @Test
    fun `searchResponseDto Moshi serialization and deserialization - empty items`() {
        val originalDto = SearchResponseDto(
            totalCount = 0,
            incompleteResults = true,
            items = emptyList()
        )

        val jsonString = adapter.toJson(originalDto)
        assertNotNull(jsonString)

        val deserializedDto = adapter.fromJson(jsonString)
        assertNotNull(deserializedDto)
        assertEquals(originalDto, deserializedDto)
    }

    @Test
    fun `searchResponseDto Moshi deserialization from sample JSON string`() {
        val jsonString = """{
            "total_count": 125,
            "incomplete_results": false,
            "items": [
                {
                    "login": "testuser1",
                    "id": 1,
                    "avatar_url": "url1",
                    "type": "User"
                },
                {
                    "login": "testuser2",
                    "id": 2,
                    "avatar_url": "url2",
                    "site_admin": true
                }
            ]
        }"""

        val deserializedDto = adapter.fromJson(jsonString)
        assertNotNull(deserializedDto)
        assertEquals(125, deserializedDto?.totalCount)
        assertEquals(false, deserializedDto?.incompleteResults)
        assertEquals(2, deserializedDto?.items?.size)

        val item1 = deserializedDto?.items?.get(0)
        assertNotNull(item1)
        assertEquals("testuser1", item1?.login)
        assertEquals(1, item1?.id)
        assertEquals("url1", item1?.avatarUrl)
        assertEquals("User", item1?.type)

        val item2 = deserializedDto?.items?.get(1)
        assertNotNull(item2)
        assertEquals("testuser2", item2?.login)
        assertEquals(2, item2?.id)
        assertEquals("url2", item2?.avatarUrl)
        assertEquals(true, item2?.siteAdmin)
        assertNull(item2?.type) // Type not present in JSON for item2, should be null
    }
}
