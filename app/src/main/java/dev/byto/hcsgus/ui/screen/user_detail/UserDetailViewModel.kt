package dev.byto.hcsgus.ui.screen.user_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.byto.hcsgus.core.base.BaseViewModel
import dev.byto.hcsgus.domain.model.UserDetailState
import dev.byto.hcsgus.domain.usecase.GetUserDetailUseCase
import dev.byto.hcsgus.util.GlobalErrorHandler
import dev.byto.hcsgus.util.constant.Constants.ERROR_MESSAGE_KEY
import dev.byto.hcsgus.util.constant.Constants.USERNAME_KEY
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed class for one-time events (if any were needed).
sealed class UserDetailEvent

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<UserDetailState, UserDetailEvent>() {

    // Initial state of the ViewModel
    override fun createInitialState(): UserDetailState = UserDetailState()

    init {
        // Retrieve the username passed via navigation
        val username: String? = savedStateHandle[USERNAME_KEY]
        if (username != null) {
            fetchUserDetail(username)
        } else {
            // Set an error if the username argument is missing
            val appError =
                GlobalErrorHandler.mapToAppError(IllegalArgumentException(ERROR_MESSAGE_KEY))
            setError(appError)
        }
    }

    private fun fetchUserDetail(username: String) {
        viewModelScope.launch {
            // Set state to Loading when starting the fetch
            setState { copy(isLoading = true) }

            getUserDetailUseCase(username)
                .onEach { result ->
                    result.fold(
                        onSuccess = { userDetail ->
                            setState { copy(isLoading = false, data = userDetail) }

                        },
                        onFailure = { throwable ->
                            val appError = GlobalErrorHandler.mapToAppError(throwable)
                            setState { copy(isLoading = false) }
                            setError(appError)
                        }
                    )
                }.launchIn(viewModelScope)
        }
    }
}