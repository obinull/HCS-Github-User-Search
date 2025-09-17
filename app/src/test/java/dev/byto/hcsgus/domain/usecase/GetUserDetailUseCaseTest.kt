package dev.byto.hcsgus.domain.usecase

import dev.byto.hcsgus.domain.model.UserDetail
import dev.byto.hcsgus.domain.repository.UserRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Assuming a UserDetail structure like this for testing purposes:
// data class UserDetail(
//    val login: String,
//    val name: String?,
//    val avatarUrl: String,
//    val followers: Int,
//    val following: Int,
//    val publicRepos: Int,
//    val bio: String?
// )

@ExperimentalCoroutinesApi
class GetUserDetailUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var getUserDetailUseCase: GetUserDetailUseCase

    private val testUsername = "testuser"
    private val sampleUserDetail = UserDetail(
        id = 12345, // Added dummy ID
        login = "testuser",
        avatarUrl = "https://example.com/avatar.png",
        name = "Test User Name",
        company = "Test Company Inc.", // Added dummy company
        location = "San Francisco, CA", // Added dummy location
        bio = "This is a detailed test bio. I enjoy coding and exploring new technologies. Currently working on an exciting Android project!",
        blog = "https://testuserblog.example.com", // Added dummy blog URL
        followers = 150,
        following = 75,
        publicRepos = 25
    )

    @Before
    fun setUp() {
        userRepository = mockk()
        getUserDetailUseCase = GetUserDetailUseCase(userRepository)
    }

    @Test
    fun `invoke with username success - returns flow with success result`() = runTest {
        // Arrange
        every { userRepository.getUserDetail(testUsername) } returns flowOf(Result.success(sampleUserDetail))

        // Act
        val resultFlow = getUserDetailUseCase(testUsername)

        // Assert
        coVerify(exactly = 1) { userRepository.getUserDetail(testUsername) }

        resultFlow.collect {
            assertTrue(it.isSuccess)
            assertEquals(sampleUserDetail, it.getOrNull())
        }
        // Or using first() if you expect a single emission from this flow behavior
        val firstResult = resultFlow.first()
        assertTrue(firstResult.isSuccess)
        assertEquals(sampleUserDetail, firstResult.getOrNull())
    }

    @Test
    fun `invoke with username failure - returns flow with failure result`() = runTest {
        // Arrange
        val exception = RuntimeException("Network request failed")
        every { userRepository.getUserDetail(testUsername) } returns flowOf(Result.failure(exception))

        // Act
        val resultFlow = getUserDetailUseCase(testUsername)

        // Assert
        coVerify(exactly = 1) { userRepository.getUserDetail(testUsername) }

        resultFlow.collect {
            assertTrue(it.isFailure)
            assertEquals(exception, it.exceptionOrNull())
        }
        // Or using first()
        val firstResult = resultFlow.first()
        assertTrue(firstResult.isFailure)
        assertEquals(exception, firstResult.exceptionOrNull())
    }
}
