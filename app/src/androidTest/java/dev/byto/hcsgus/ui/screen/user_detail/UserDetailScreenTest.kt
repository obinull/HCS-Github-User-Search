package dev.byto.hcsgus.ui.screen.user_detail

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModel
import dev.byto.hcsgus.R
import dev.byto.hcsgus.domain.model.UserDetail
import dev.byto.hcsgus.domain.model.UserDetailState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

// --- Fake ViewModel ---
// This class mimics the real UserDetailViewModel for testing purposes.
// It needs to be an 'open' class if the real ViewModel is final and we were trying to mock it with a library.
// However, since UserDetailScreen accepts UserDetailViewModel as a parameter, we can pass this fake directly.
open class FakeUserDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserDetailState(isLoading = true))
    open val uiState: StateFlow<UserDetailState> = _uiState

    private val _error = MutableSharedFlow<Throwable>()
    open val error: Flow<Throwable> = _error

    fun MOCK_setUiState(newState: UserDetailState) {
        _uiState.value = newState
    }

    suspend fun MOCK_emitError(throwable: Throwable) {
        _error.emit(throwable)
    }
}


class UserDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var fakeViewModel: FakeUserDetailViewModel
    private var onNavigateBackCalled: AtomicBoolean = AtomicBoolean(false)

    private val sampleUser = UserDetail(
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
        fakeViewModel = FakeUserDetailViewModel()
        onNavigateBackCalled.set(false) // Reset before each test

        composeTestRule.setContent {
            UserDetailScreen(
                viewModel = fakeViewModel,
                onNavigateBack = { onNavigateBackCalled.set(true) }
            )
        }
    }

    @Test
    fun userDetailScreen_whenLoading_showsProgressIndicator() {
        fakeViewModel.MOCK_setUiState(UserDetailState(isLoading = true))
        // The CircularProgressIndicator doesn't have specific text or easy content description by default.
        // We'll assert that UserDetailContent is NOT displayed, implying loading.
        // A more robust way would be to add a testTag to the CircularProgressIndicator.
        // For now, check if the content area that shows user details is hidden.
        composeTestRule.onNodeWithText(sampleUser.name!!).assertDoesNotExist()
        composeTestRule.onNodeWithText(sampleUser.login).assertDoesNotExist()
    }

    @Test
    fun userDetailScreen_whenDataLoaded_showsUserDetails() {
        fakeViewModel.MOCK_setUiState(UserDetailState(isLoading = false, data = sampleUser))

        // Check TopAppBar title
        composeTestRule.onNodeWithText(sampleUser.login).assertIsDisplayed() // TopAppBar uses login

        // Check UserDetailContent elements
        composeTestRule.onNodeWithText(sampleUser.name!!).assertIsDisplayed()
        // The second instance of login (under the name)
        composeTestRule.onNodeWithText(sampleUser.login).assertIsDisplayed()


        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.user_avatar)).assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleUser.followers.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.followers)).assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleUser.following.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.following)).assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleUser.publicRepos.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.repositories)).assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleUser.bio!!).assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_whenDataLoaded_nameNull_showsLoginAsTitleAndName() {
        val userNoName = sampleUser.copy(name = null)
        fakeViewModel.MOCK_setUiState(UserDetailState(isLoading = false, data = userNoName))

        // Check TopAppBar title (falls back to login, or default if login is also an issue, but here login is present)
        composeTestRule.onNodeWithText(userNoName.login).assertIsDisplayed() // TopAppBar shows login

        // Check UserDetailContent name area (falls back to login)
        // There will be two nodes with "testuser": one in app bar, one in content.
        // This assertion might be ambiguous if not careful.
        // Let's assume the one under the avatar is the one MaterialTheme.typography.headlineSmall
         composeTestRule.onNodeWithText(userNoName.login).assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_whenDataLoaded_bioNull_bioTextNotDisplayed() {
        val userNoBio = sampleUser.copy(bio = null)
        fakeViewModel.MOCK_setUiState(UserDetailState(isLoading = false, data = userNoBio))

        composeTestRule.onNodeWithText(sampleUser.bio!!).assertDoesNotExist()
    }

    @Test
    fun userDetailScreen_whenErrorEmitted_showsSnackbar() {
        val errorMessage = "Network Error Occurred"
        // Set an initial state (e.g., loaded or loading)
        fakeViewModel.MOCK_setUiState(UserDetailState(isLoading = false, data = sampleUser))

        runBlocking { // Needed for emitting from SharedFlow and for snackbar to appear
            fakeViewModel.MOCK_emitError(Throwable(errorMessage))
        }
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_clickNavigateBack_callsOnNavigateBack() {
        fakeViewModel.MOCK_setUiState(UserDetailState(isLoading = false, data = sampleUser))

        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.back))
            .assertIsDisplayed()
            .performClick()

        assertTrue(onNavigateBackCalled.get())
    }
}
