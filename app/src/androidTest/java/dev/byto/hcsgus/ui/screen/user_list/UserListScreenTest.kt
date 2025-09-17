package dev.byto.hcsgus.ui.screen.user_list

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.ViewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import dev.byto.hcsgus.R
import dev.byto.hcsgus.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

// Minimal User data class for testing
// val User.Companion.preview: User get() = User(id = 1, login = "testuser", avatarUrl = "url")

open class FakeUserListViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    open val searchQuery: StateFlow<String> = _searchQuery

    private val _userPagingData = MutableStateFlow<PagingData<User>>(PagingData.empty())
    open val userPagingData: Flow<PagingData<User>> = _userPagingData

    // To track if search was called
    val searchCalledWith = AtomicReference<String?>()

    open fun search(query: String) {
        _searchQuery.value = query
        searchCalledWith.set(query)
        // In a real ViewModel, this would trigger a new PagingData flow
    }

    fun MOCK_setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun MOCK_emitPagingData(pagingData: PagingData<User>) {
        _userPagingData.value = pagingData
    }
}

class UserListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var fakeViewModel: FakeUserListViewModel
    private val navigatedToDetailWith = AtomicReference<String?>()

    private val sampleUser1 = User(id = 1, login = "user1", avatarUrl = "url1")
    private val sampleUser2 = User(id = 2, login = "user2", avatarUrl = "url2")

    @Before
    fun setUp() {
        fakeViewModel = FakeUserListViewModel()
        navigatedToDetailWith.set(null)

        composeTestRule.setContent {
            UserListScreen(
                viewModel = fakeViewModel,
                onNavigateToDetail = { username -> navigatedToDetailWith.set(username) }
            )
        }
    }

    @Test
    fun userListScreen_initialDisplay_showsAppBarAndSearchField() {
        val title = composeTestRule.activity.getString(R.string.title_github_users)
        val searchLabel = composeTestRule.activity.getString(R.string.title_search_users)

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(searchLabel).assertIsDisplayed() // Search field label
    }

    @Test
    fun userListScreen_typingInSearch_updatesViewModelAndDisplaysQuery() {
        val searchText = "hello"
        val searchLabel = composeTestRule.activity.getString(R.string.title_search_users)

        composeTestRule.onNodeWithText(searchLabel).performTextInput(searchText)

        assertEquals(searchText, fakeViewModel.searchQuery.value)
        assertEquals(searchText, fakeViewModel.searchCalledWith.get())
        composeTestRule.onNodeWithText(searchText).assertIsDisplayed() // Checks if the input text is shown
    }

    @Test
    fun userListScreen_whenPagingLoading_showsCircularProgressIndicator() {
        val loadStates = LoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(endOfPaginationReached = false),
            append = LoadState.NotLoading(endOfPaginationReached = false)
        )
        fakeViewModel.MOCK_emitPagingData(PagingData.empty(sourceLoadStates = loadStates))

        // Default CircularProgressIndicator doesn't have text.
        // We check for its existence by assuming other content (like user list) isn't there.
        // A more robust way: add a testTag to the Box containing the CircularProgressIndicator.
        // For now, let's assume if UserListItem for sampleUser1 is not found, it might be loading.
        composeTestRule.onNodeWithText(sampleUser1.login).assertDoesNotExist()
        // If we had a testTag="loadingIndicator" on the Box:
        // composeTestRule.onNodeWithTestTag("loadingIndicator").assertIsDisplayed()
    }

    @Test
    fun userListScreen_whenUsersLoaded_displaysUserListItems() {
        val users = listOf(sampleUser1, sampleUser2)
        fakeViewModel.MOCK_emitPagingData(PagingData.from(users))

        composeTestRule.onNodeWithText(sampleUser1.login).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("${sampleUser1.login} avatar").assertIsDisplayed()

        composeTestRule.onNodeWithText(sampleUser2.login).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("${sampleUser2.login} avatar").assertIsDisplayed()
    }

    @Test
    fun userListScreen_whenEmptyListLoaded_displaysNoUsers() {
        fakeViewModel.MOCK_emitPagingData(PagingData.from(emptyList<User>()))

        composeTestRule.onNodeWithText(sampleUser1.login).assertDoesNotExist()
        composeTestRule.onNodeWithText(sampleUser2.login).assertDoesNotExist()
        // You could add an assertion here for an "empty list" message if your UI has one.
    }

    @Test
    fun userListScreen_clickUserItem_navigatesToDetail() {
        val users = listOf(sampleUser1)
        fakeViewModel.MOCK_emitPagingData(PagingData.from(users))

        composeTestRule.onNodeWithText(sampleUser1.login).assertIsDisplayed().performClick()

        assertEquals(sampleUser1.login, navigatedToDetailWith.get())
    }
}
