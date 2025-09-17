package dev.byto.hcsgus.ui.screen.user_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.byto.hcsgus.domain.model.User
import dev.byto.hcsgus.domain.usecase.GetUsersUseCase
import dev.byto.hcsgus.domain.usecase.SearchUsersUseCase
import dev.byto.hcsgus.util.constant.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {

    // Expose searchQuery as StateFlow for the Compose TextField
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    /**
     * A public Flow that emits PagingData.
     * It listens to changes in `_searchQuery`, waits for a 500ms pause in typing (debounce),
     * and then fetches the appropriate user list.
     * - If the query is blank, it gets the general user list.
     * - If the query has text, it performs a search.
     * The `cachedIn(viewModelScope)` operator caches the data, making it survive configuration changes.
     */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val userPagingData: Flow<PagingData<User>> = _searchQuery
        .debounce(500L) // Avoids rapid API calls while the user is typing.
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getUsersUseCase()
            } else {
                searchUsersUseCase(query)
            }
        }
        .cachedIn(viewModelScope)

    /**
     * Called by the UI to update the search query.
     * @param query The new search text from the user.
     */
    fun search(query: String) {
        _searchQuery.value = query
    }
}