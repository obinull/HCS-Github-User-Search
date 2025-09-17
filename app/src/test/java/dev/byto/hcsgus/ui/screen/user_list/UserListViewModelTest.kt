package dev.byto.hcsgus.ui.screen.user_list

import androidx.paging.PagingData
import app.cash.turbine.test
import dev.byto.hcsgus.domain.model.User
import dev.byto.hcsgus.domain.usecase.GetUsersUseCase
import dev.byto.hcsgus.domain.usecase.SearchUsersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// Minimal User data class for testing PagingData
// data class User(val id: Int, val login: String, val avatarUrl: String)

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    val testDispatcher: StandardTestDispatcher = StandardTestDispatcher(TestCoroutineScheduler()),
) : TestWatcher() {
    val testScheduler: TestCoroutineScheduler
        get() = testDispatcher.scheduler

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class UserListViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: UserListViewModel
    private lateinit var getUsersUseCase: GetUsersUseCase
    private lateinit var searchUsersUseCase: SearchUsersUseCase

    private val sampleUser1 = User(id = 1, login = "user1", avatarUrl = "url1")
    private val sampleUser2 = User(id = 2, login = "user2", avatarUrl = "url2")

    private val pagingDataUser1 = PagingData.from(listOf(sampleUser1))
    private val pagingDataUser2 = PagingData.from(listOf(sampleUser2))
    private val emptyPagingData = PagingData.empty<User>()

    @Before
    fun setUp() {
        getUsersUseCase = mockk()
        searchUsersUseCase = mockk()

        // Default behavior for use cases
        coEvery { getUsersUseCase() } returns flowOf(emptyPagingData)
        coEvery { searchUsersUseCase(any()) } returns flowOf(emptyPagingData)

        viewModel = UserListViewModel(getUsersUseCase, searchUsersUseCase)
    }

    @Test
    fun `initial state - searchQuery is empty`() {
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `initial state - userPagingData calls getUsersUseCase after debounce`() = runTest(mainCoroutineRule.testDispatcher) {
        coEvery { getUsersUseCase() } returns flowOf(pagingDataUser1)

        // Re-initialize ViewModel to capture initial flatMapLatest emission with new mock
        viewModel = UserListViewModel(getUsersUseCase, searchUsersUseCase)

        val job = launch {
            viewModel.userPagingData.test {
                mainCoroutineRule.testScheduler.advanceTimeBy(500L) // Advance past debounce
                val emittedItem = awaitItem()
                assertNotNull(emittedItem) // We expect PagingData from getUsersUseCase
                // More specific assertions on PagingData content are complex; focus on source
                cancelAndConsumeRemainingEvents()
            }
        }
        job.join()
        coVerify(exactly = 1) { getUsersUseCase() }
        coVerify(exactly = 0) { searchUsersUseCase(any()) }
    }

    @Test
    fun `search function - updates searchQuery`() {
        val testQuery = "testquery"
        viewModel.search(testQuery)
        assertEquals(testQuery, viewModel.searchQuery.value)
    }

    @Test
    fun `userPagingData - blank query calls getUsersUseCase`() = runTest(mainCoroutineRule.testDispatcher) {
        coEvery { getUsersUseCase() } returns flowOf(pagingDataUser1)

        viewModel.search("") // Set to blank
        mainCoroutineRule.testScheduler.advanceTimeBy(500L)

        // Collect to ensure the flow processes
        val result = viewModel.userPagingData.first()
        assertNotNull(result)

        coVerify(atLeast = 1) { getUsersUseCase() } // Called at init and after search
        coVerify(exactly = 0) { searchUsersUseCase(any()) }
    }

    @Test
    fun `userPagingData - non-blank query calls searchUsersUseCase`() = runTest(mainCoroutineRule.testDispatcher) {
        val testQuery = "searchMe"
        coEvery { searchUsersUseCase(testQuery) } returns flowOf(pagingDataUser2)

        viewModel.search(testQuery)
        mainCoroutineRule.testScheduler.advanceTimeBy(500L)

        val result = viewModel.userPagingData.first()
        assertNotNull(result)

        coVerify { searchUsersUseCase(testQuery) }
    }

    @Test
    fun `userPagingData - debounce behavior`() = runTest(mainCoroutineRule.testDispatcher) {
        val query1 = "q1"
        val query2 = "q2"
        val queryFinal = "finalQuery"

        coEvery { searchUsersUseCase(queryFinal) } returns flowOf(pagingDataUser1)

        viewModel.search(query1)
        mainCoroutineRule.testScheduler.advanceTimeBy(100L) // Less than debounce
        viewModel.search(query2)
        mainCoroutineRule.testScheduler.advanceTimeBy(200L) // Less than debounce
        viewModel.search(queryFinal)
        mainCoroutineRule.testScheduler.advanceTimeBy(500L) // Past debounce for finalQuery

        val result = viewModel.userPagingData.first() // Trigger collection
        assertNotNull(result)

        coVerify(exactly = 0) { searchUsersUseCase(query1) }
        coVerify(exactly = 0) { searchUsersUseCase(query2) }
        coVerify(exactly = 1) { searchUsersUseCase(queryFinal) }
        // getUsersUseCase might have been called once on init if searchQuery was initially blank
        // and if it wasn't overridden by the time the first collection happened.
        // For this test, we are mainly concerned with searchUsersUseCase calls.
    }
}
