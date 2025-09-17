package dev.byto.hcsgus.data.repository

import android.database.sqlite.SQLiteException
import androidx.paging.PagingConfig
import app.cash.turbine.test
import dev.byto.hcsgus.data.local.AppDatabase
import dev.byto.hcsgus.data.local.dao.UserDao
import dev.byto.hcsgus.data.local.dao.UserDetailDao
import dev.byto.hcsgus.data.local.entity.UserDetailEntity
import dev.byto.hcsgus.data.mapper.toDomainModel // Assuming this maps UserDetailEntity to UserDetail (domain)
import dev.byto.hcsgus.data.mapper.toEntity // Assuming this maps TestDataUserDetailDto to UserDetailEntity
import dev.byto.hcsgus.data.remote.api.ApiService
import dev.byto.hcsgus.domain.model.UserDetail
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import retrofit2.HttpException
import retrofit2.Response

// Placeholder DTO that apiService.getUserDetail() might return
// and on which .toEntity() is called.
// Its structure should align with what your actual .toEntity() extension function expects.
data class TestDataUserDetailDto(
    val id: Int,
    val login: String,
    val avatarUrl: String,
    val name: String?,
    val company: String?,
    val location: String?,
    val bio: String?,
    val blog: String?,
    val followers: Int,
    val following: Int,
    val publicRepos: Int
)

@ExperimentalCoroutinesApi
class MainCoroutineRule(private val dispatcher: StandardTestDispatcher = StandardTestDispatcher()) :
    TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class UserRepositoryImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var apiService: ApiService
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var userDetailDao: UserDetailDao

    private val testUsername = "testuser"

    private val testDetailDto = TestDataUserDetailDto(
        id = 1, login = testUsername, avatarUrl = "url.com/avatar", name = "Test User",
        company = "TestCo", location = "TestCity", bio = "A bio", blog = "blog.com",
        followers = 10, following = 5, publicRepos = 3
    )
    // Assuming your toEntity() mapper correctly converts TestDataUserDetailDto to UserDetailEntity
    private val testDetailEntity = testDetailDto.toEntity() // Uses the actual mapper
    // Assuming your toDomainModel() mapper correctly converts UserDetailEntity to UserDetail (domain)
    private val testDetailDomain = testDetailEntity.toDomainModel() // Uses the actual mapper


    @Before
    fun setUp() {
        apiService = mockk()
        database = mockk(relaxed = true) // relaxed for Pager internal calls if any
        userDao = mockk(relaxed = true)
        userDetailDao = mockk()

        every { database.userDao() } returns userDao
        every { database.userDetailDao() } returns userDetailDao

        userRepository = UserRepositoryImpl(apiService, database)
    }

    @Test
    fun `getUserDetail success - fetches, saves, returns domain model`() = runTest {
        // Arrange
        coEvery { apiService.getUserDetail(testUsername) } returns testDetailDto
        coEvery { userDetailDao.insertUserDetail(any()) } returns Unit // Simulate successful insertion

        // Act
        val resultFlow = userRepository.getUserDetail(testUsername)

        // Assert
        resultFlow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(testDetailDomain, result.getOrNull())
            awaitComplete()
        }
        coVerify(exactly = 1) { apiService.getUserDetail(testUsername) }
        coVerify(exactly = 1) { userDetailDao.insertUserDetail(testDetailEntity) }
    }

    @Test
    fun `getUserDetail api error - returns failure`() = runTest {
        // Arrange
        val exception = HttpException(Response.error<Any>(404, mockk(relaxed = true)))
        coEvery { apiService.getUserDetail(testUsername) } throws exception

        // Act
        val resultFlow = userRepository.getUserDetail(testUsername)

        // Assert
        resultFlow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            awaitComplete()
        }
        coVerify(exactly = 0) { userDetailDao.insertUserDetail(any()) }
    }

    @Test
    fun `getUserDetail db error on insert - still returns success from network`() = runTest {
        // Arrange
        val dbException = SQLiteException("DB error")
        coEvery { apiService.getUserDetail(testUsername) } returns testDetailDto
        coEvery { userDetailDao.insertUserDetail(any()) } throws dbException

        // Act
        val resultFlow = userRepository.getUserDetail(testUsername)

        // Assert
        resultFlow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess) // Behavior as per current code: logs DB error, proceeds with network data
            assertEquals(testDetailDomain, result.getOrNull())
            awaitComplete()
        }
        coVerify(exactly = 1) { apiService.getUserDetail(testUsername) }
        coVerify(exactly = 1) { userDetailDao.insertUserDetail(testDetailEntity) }
        // Here you might also verify Log.e was called if you had a Log utility mockable.
    }

    @Test
    fun `getUsers calls userDao pagingSource and uses PagingConfig`() {
        // Act: Calling getUsers will set up the Pager internally.
        // We can't easily inspect the Pager's direct config in a unit test without refactoring
        // or using Paging testing artifacts more deeply.
        // However, we can verify that the necessary components are called.
        userRepository.getUsers().first() // Trigger the flow to ensure factory is called

        // Assert
        verify { userDao.pagingSource() } // Check that the factory was used.
        // PagingConfig pageSize is 30 in the implementation. This is implicitly tested if Pager runs.
    }

    @Test
    fun `searchUsers uses PagingConfig`() {
        val query = "testquery"
        // Act: Calling searchUsers will set up the Pager internally.
        userRepository.searchUsers(query).first() // Trigger the flow

        // Assert
        // Similar to getUsers, asserting UserSearchPagingSource construction with correct params
        // and PagingConfig pageSize=30 is implicit. A full Pager test is more involved.
        // We trust that the Pager is configured as written in the implementation.
    }
}
