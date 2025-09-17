package dev.byto.hcsgus.data.remote.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.byto.hcsgus.data.remote.dto.SearchResponseDto
import dev.byto.hcsgus.data.remote.dto.UserDetailDto
import dev.byto.hcsgus.data.remote.dto.UserDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@ExperimentalCoroutinesApi
class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // --- getUsers Tests ---_/
    @Test
    fun `getUsers success - correct request and response`() = runTest {
        val since = 0
        val perPage = 10
        val mockUserListJson = """[
            {"id": 1, "login": "user1", "avatar_url": "url1"},
            {"id": 2, "login": "user2", "avatar_url": "url2"}
        ]"""
        mockWebServer.enqueue(MockResponse().setBody(mockUserListJson).setResponseCode(200))

        val response: List<UserDto> = apiService.getUsers(since, perPage)

        val request = mockWebServer.takeRequest()
        assertEquals("/users?since=$since&per_page=$perPage", request.path)
        assertEquals("GET", request.method)

        assertEquals(2, response.size)
        assertEquals(1, response[0].id)
        assertEquals("user1", response[0].login)
    }

    @Test
    fun `getUsers error - throws HttpException`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        try {
            apiService.getUsers(0, 10)
            throw AssertionError("HttpException was expected but not thrown.")
        } catch (e: HttpException) {
            assertEquals(404, e.code())
        }
    }

    // --- searchUsers Tests --- //
    @Test
    fun `searchUsers success - correct request and response`() = runTest {
        val query = "kotlin"
        val page = 1
        val perPage = 5
        val mockSearchResponseJson = """{
            "total_count": 1,
            "incomplete_results": false,
            "items": [{"id": 3, "login": "kotlin_dev", "score": 1.0}]
        }"""
        mockWebServer.enqueue(MockResponse().setBody(mockSearchResponseJson).setResponseCode(200))

        val response: SearchResponseDto = apiService.searchUsers(query, page, perPage)

        val request = mockWebServer.takeRequest()
        assertEquals("/search/users?q=$query&page=$page&per_page=$perPage", request.path)
        assertEquals("GET", request.method)

        assertEquals(1, response.totalCount)
        assertEquals(false, response.incompleteResults)
        assertEquals(1, response.items.size)
        assertEquals(3, response.items[0].id)
        assertEquals("kotlin_dev", response.items[0].login)
    }

    @Test
    fun `searchUsers error - throws HttpException`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        try {
            apiService.searchUsers("query", 1, 5)
            throw AssertionError("HttpException was expected but not thrown.")
        } catch (e: HttpException) {
            assertEquals(500, e.code())
        }
    }

    // --- getUserDetail Tests --- //
    @Test
    fun `getUserDetail success - correct request and response`() = runTest {
        val username = "octocat"
        val mockUserDetailJson = """{
            "id": 4, "login": "octocat", "name": "The Octocat", "followers": 1000
        }"""
        mockWebServer.enqueue(MockResponse().setBody(mockUserDetailJson).setResponseCode(200))

        val response: UserDetailDto = apiService.getUserDetail(username)

        val request = mockWebServer.takeRequest()
        assertEquals("/users/$username", request.path)
        assertEquals("GET", request.method)

        assertEquals(4, response.id)
        assertEquals("octocat", response.login)
        assertEquals("The Octocat", response.name)
        assertEquals(1000, response.followers)
    }

    @Test
    fun `getUserDetail 404 error - throws HttpException`() = runTest {
        val username = "nonexistentuser"
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        try {
            apiService.getUserDetail(username)
            throw AssertionError("HttpException was expected but not thrown.")
        } catch (e: HttpException) {
            assertEquals(404, e.code())
        }
    }
}
