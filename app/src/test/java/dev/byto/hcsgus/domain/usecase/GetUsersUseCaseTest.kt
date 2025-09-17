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
class GetUsersUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var getUsersUseCase: GetUsersUseCase

    private val sampleUser = User(id = 1, login = "defaultuser", avatarUrl = "default_url")
    private val testPagingDataFlow = flowOf(PagingData.from(listOf(sampleUser)))

    @Before
    fun setUp() {
        userRepository = mockk()
        getUsersUseCase = GetUsersUseCase(userRepository)
    }

    @Test
    fun `invoke calls userRepository getUsers and returns its flow`() = runTest {
        // Arrange: userRepository.getUsers() will return testPagingDataFlow
        every { userRepository.getUsers() } returns testPagingDataFlow

        // Act: Call the use case
        val resultFlow = getUsersUseCase()

        // Assert: Verify the interaction with the repository
        coVerify(exactly = 1) { userRepository.getUsers() }

        // Assert: Verify the returned flow is the one from the repository
        assertEquals(testPagingDataFlow.first(), resultFlow.first())
        // We can also check if the flow objects are the same instance if that's critical,
        // but checking content (or reference if the mock returns the exact same flow instance) is usually sufficient.
        assertEquals(testPagingDataFlow, resultFlow)
    }
}
