package dev.byto.hcsgus.ui.screen.user_detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.byto.hcsgus.domain.model.UserDetail
import dev.byto.hcsgus.domain.usecase.GetUserDetailUseCase
import dev.byto.hcsgus.util.GlobalErrorHandler
import dev.byto.hcsgus.util.constant.Constants
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

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

class UserDetailViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: UserDetailViewModel
    private lateinit var getUserDetailUseCase: GetUserDetailUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private val testUsername = "testuser"
    private val sampleUserDetail = UserDetail(
        login = "testuser",
        avatarUrl = "https://example.com/avatar.png",
        name = "Test User Name",
        followers = 100,
        following = 50,
        publicRepos = 20,
        bio = "This is a test bio.",
        id = 1,
        company = "Test Company",
        location = "Test Location",
        blog = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor "
    )

    @Before
    fun setUp() {
        getUserDetailUseCase = mockk()
    }

    @Test
    fun `init with username success - uiState updates correctly and no error`() = runTest {
        // Arrange
        savedStateHandle = SavedStateHandle().apply { set(Constants.USERNAME_KEY, testUsername) }
        coEvery { getUserDetailUseCase(testUsername) } returns flowOf(Result.success(sampleUserDetail))

        // Act
        viewModel = UserDetailViewModel(getUserDetailUseCase, savedStateHandle)

        // Assert
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading) // Initial state set by init block triggering fetch
            assertNull(initialState.data)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(sampleUserDetail, successState.data)
            cancelAndConsumeRemainingEvents()
        }
        viewModel.error.test {
            expectNoEvents() // No error should be emitted
        }
    }

    @Test
    fun `init with username failure - uiState updates and error emitted`() = runTest {
        // Arrange
        savedStateHandle = SavedStateHandle().apply { set(Constants.USERNAME_KEY, testUsername) }
        val exception = RuntimeException("Network error")
        val expectedAppError = GlobalErrorHandler.mapToAppError(exception)
        coEvery { getUserDetailUseCase(testUsername) } returns flowOf(Result.failure(exception))

        // Act
        viewModel = UserDetailViewModel(getUserDetailUseCase, savedStateHandle)

        // Assert
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            assertNull(initialState.data)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNull(errorState.data) // Data should remain null or be cleared on error
            cancelAndConsumeRemainingEvents()
        }
        viewModel.error.test {
            val emittedError = awaitItem()
            assertEquals(expectedAppError.message, emittedError.message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `init without username - error emitted and uiState has no data`() = runTest {
        // Arrange
        savedStateHandle = SavedStateHandle() // No username
        val expectedAppError = GlobalErrorHandler.mapToAppError(IllegalArgumentException(Constants.ERROR_MESSAGE_KEY))

        // Act
        viewModel = UserDetailViewModel(getUserDetailUseCase, savedStateHandle)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem() // Should be initial state
            assertFalse(state.isLoading) // Should not be loading as fetchUserDetail is not called
            assertNull(state.data)
            cancelAndConsumeRemainingEvents()
        }
        viewModel.error.test {
            val emittedError = awaitItem()
            assertEquals(expectedAppError.message, emittedError.message)
            cancelAndConsumeRemainingEvents()
        }
    }
}
