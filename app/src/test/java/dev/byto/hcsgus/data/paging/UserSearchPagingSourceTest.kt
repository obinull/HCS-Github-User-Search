package dev.byto.hcsgus.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.byto.hcsgus.data.mapper.toDomainModel // Assuming UserEntity.toDomainModel() -> User
import dev.byto.hcsgus.data.mapper.toEntity // Assuming UserDto.toEntity() -> UserEntity
import dev.byto.hcsgus.data.remote.api.ApiService
import dev.byto.hcsgus.data.remote.dto.SearchResponseDto
import dev.byto.hcsgus.data.remote.dto.UserDto
import dev.byto.hcsgus.domain.model.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class UserSearchPagingSourceTest {

    private lateinit var apiService: ApiService
    private lateinit var pagingSource: UserSearchPagingSource

    private val testQuery = "github"
    private val pageSize = 20

    private fun createUserDto(id: Int, login: String): UserDto {
        return UserDto(id = id, login = login, avatarUrl = "avatar_url_$id", score = 100 - id)
    }

    // Assuming UserDto.toEntity().toDomainModel() is the mapping chain
    private fun UserDto.toTestDomainModel(): User = this.toEntity().toDomainModel()

    @Before
    fun setUp() {
        apiService = mockk()
    }

    @Test
    fun `load - initial refresh success - returns page with data and keys`() = runTest {
        pagingSource = UserSearchPagingSource(apiService, testQuery)
        val userDtoList = listOf(createUserDto(1, "user1"), createUserDto(2, "user2"))
        val searchResponse = SearchResponseDto(totalCount = 2, incompleteResults = false, items = userDtoList)
        coEvery { apiService.searchUsers(testQuery, 1, pageSize) } returns searchResponse

        val expectedData = userDtoList.map { it.toTestDomainModel() }

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = pageSize, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(expectedData, pageResult.data)
        assertNull(pageResult.prevKey)
        assertEquals(2, pageResult.nextKey) // page + 1
    }

    @Test
    fun `load - append success - returns page with data and keys`() = runTest {
        pagingSource = UserSearchPagingSource(apiService, testQuery)
        val currentPage = 2
        val userDtoList = listOf(createUserDto(3, "user3"), createUserDto(4, "user4"))
        val searchResponse = SearchResponseDto(totalCount = 4, incompleteResults = false, items = userDtoList)
        coEvery { apiService.searchUsers(testQuery, currentPage, pageSize) } returns searchResponse

        val expectedData = userDtoList.map { it.toTestDomainModel() }

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(key = currentPage, loadSize = pageSize, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(expectedData, pageResult.data)
        assertEquals(currentPage - 1, pageResult.prevKey) // Current key - 1 = 2 - 1 = 1
        assertEquals(currentPage + 1, pageResult.nextKey) // Current key + 1 = 2 + 1 = 3
    }

     @Test
    fun `load - prepend success - returns page with data and keys`() = runTest {
        pagingSource = UserSearchPagingSource(apiService, testQuery)
        val currentPage = 2 // Key for prepend is the page number to load before current
        val userDtoList = listOf(createUserDto(1, "user1"))
        val searchResponse = SearchResponseDto(totalCount = 5, incompleteResults = false, items = userDtoList)
        coEvery { apiService.searchUsers(testQuery, currentPage, pageSize) } returns searchResponse

        val expectedData = userDtoList.map { it.toTestDomainModel() }

        val result = pagingSource.load(
            PagingSource.LoadParams.Prepend(key = currentPage, loadSize = pageSize, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertEquals(expectedData, pageResult.data)
        assertEquals(if (currentPage == 1) null else currentPage - 1, pageResult.prevKey)
        assertEquals(currentPage + 1, pageResult.nextKey)
    }

    @Test
    fun `load - blank query - returns empty page`() = runTest {
        pagingSource = UserSearchPagingSource(apiService, "   ") // Blank query

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = pageSize, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertTrue(pageResult.data.isEmpty())
        assertNull(pageResult.prevKey)
        assertNull(pageResult.nextKey)
    }

    @Test
    fun `load - api IOException - returns error result`() = runTest {
        pagingSource = UserSearchPagingSource(apiService, testQuery)
        val exception = IOException("Network error")
        coEvery { apiService.searchUsers(any(), any(), any()) } throws exception

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = pageSize, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(exception, (result as PagingSource.LoadResult.Error).throwable)
    }

    @Test
    fun `load - api HttpException - returns error result`() = runTest {
        pagingSource = UserSearchPagingSource(apiService, testQuery)
        val exception = HttpException(Response.error<Any>(404, mockk(relaxed = true)))
        coEvery { apiService.searchUsers(any(), any(), any()) } throws exception

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = pageSize, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(exception, (result as PagingSource.LoadResult.Error).throwable)
    }

    @Test
    fun `load - empty api response - nextKey is null`() = runTest {
        pagingSource = UserSearchPagingSource(apiService, testQuery)
        val searchResponse = SearchResponseDto(totalCount = 0, incompleteResults = false, items = emptyList())
        coEvery { apiService.searchUsers(testQuery, 1, pageSize) } returns searchResponse

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = pageSize, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assertTrue(pageResult.data.isEmpty())
        assertNull(pageResult.nextKey)
    }

    @Test
    fun `getRefreshKey - returns anchorPosition minus one if prevKey null`() {
        pagingSource = UserSearchPagingSource(apiService, testQuery)
        val pages = listOf(
            PagingSource.LoadResult.Page(data = listOf(createUserDto(1, "u1").toTestDomainModel()), prevKey = null, nextKey = 2)
        )
        val state = PagingState<Int, User>(
            pages = pages,
            anchorPosition = 0, // Anchor on the first item of the first page
            config = PagingConfig(pageSize = pageSize),
            leadingPlaceholderCount = 0
        )
        // Simulate anchorPosition is at the start of the first page (index 0), closest page is that first page.
        // If prevKey of the closest page is null, it tries nextKey - 1.
        assertEquals(1, pagingSource.getRefreshKey(state)) // nextKey (2) - 1
    }

    @Test
    fun `getRefreshKey - returns anchorPosition plus one if nextKey null`() {
        pagingSource = UserSearchPagingSource(apiService, testQuery)
        val pages = listOf(
            PagingSource.LoadResult.Page(data = listOf(createUserDto(1, "u1").toTestDomainModel()), prevKey = 1, nextKey = null)
        )
        val state = PagingState<Int, User>(
            pages = pages,
            anchorPosition = 0, // Anchor on the first item of the first page
            config = PagingConfig(pageSize = pageSize),
            leadingPlaceholderCount = 0
        )
        // prevKey (1) + 1
        assertEquals(2, pagingSource.getRefreshKey(state))
    }

     @Test
    fun `getRefreshKey - anchorPosition null - returns null`() {
        pagingSource = UserSearchPagingSource(apiService, testQuery)
        val state = PagingState<Int, User>(
            pages = emptyList(),
            anchorPosition = null, // No anchor position
            config = PagingConfig(pageSize = pageSize),
            leadingPlaceholderCount = 0
        )
        assertNull(pagingSource.getRefreshKey(state))
    }
}
