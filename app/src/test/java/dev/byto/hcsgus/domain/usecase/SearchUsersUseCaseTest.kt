package dev.byto.hcsgus.domain.usecase

import androidx.paging.PagingData
import dev.byto.hcsgus.domain.model.User
import dev.byto.hcsgus.domain.repository.UserRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// Minimal User data class for testing PagingData
// data class User(val id: Int, val login: String, val avatarUrl: String)

@ExperimentalCoroutinesApi
class SearchUsersUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var searchUsersUseCase: SearchUsersUseCase

    private val sampleUser = User(id = 1, login = "testuser", avatarUrl = "url")
    private val testPagingDataFlow = flowOf(PagingData.from(listOf(sampleUser)))

    @Before
    fun setUp() {
        userRepository = mockk()
        searchUsersUseCase = SearchUsersUseCase(userRepository)
    }

    @Test
    fun `invoke calls userRepository searchUsers and returns its flow`() = runTest {
        val testQuery = "github"
        // Arrange: userRepository.searchUsers(testQuery) will return testPagingDataFlow
        every { userRepository.searchUsers(testQuery) } returns testPagingDataFlow

        // Act: Call the use case
        val resultFlow = searchUsersUseCase(testQuery)

        // Assert: Verify the interaction with the repository
        coVerify(exactly = 1) { userRepository.searchUsers(testQuery) }

        // Assert: Verify the returned flow is the one from the repository
        assertEquals(testPagingDataFlow.first(), resultFlow.first())
        // We can also check if the flow objects are the same instance if that's critical,
        // but checking content (or reference if the mock returns the exact same flow instance) is usually sufficient.
        assertEquals(testPagingDataFlow, resultFlow)
    }

    @Test
    fun `invoke with different query calls userRepository with correct query`() = runTest {
        val specificQuery = "kotlin android"
        val specificPagingDataFlow = flowOf(PagingData.from(listOf(User(2, "kotlin dev", "url2"))))
        every { userRepository.searchUsers(specificQuery) } returns specificPagingDataFlow

        val resultFlow = searchUsersUseCase(specificQuery)

        coVerify(exactly = 1) { userRepository.searchUsers(specificQuery) }
        assertEquals(specificPagingDataFlow, resultFlow)
    }
}
