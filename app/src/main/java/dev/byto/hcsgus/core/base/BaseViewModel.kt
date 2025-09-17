package dev.byto.hcsgus.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.byto.hcsgus.util.AppError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Abstract ViewModel to manage state, one-time events, and errors.
 *
 * @param State The type of the UI state data class.
 * @param Event The type of the one-time events (e.g., navigation).
 */
abstract class BaseViewModel<State, Event> : ViewModel() {

    // A private lazy-initialized property to hold the initial state.
    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    // The public, read-only state flow that the UI observes.
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    // A channel for one-time events like navigation or showing a toast.
    private val _event: Channel<Event> = Channel()
    val event = _event.receiveAsFlow()

    // A shared flow for emitting errors that the UI can collect and display.
    private val _error = MutableSharedFlow<AppError>()
    val error = _error.asSharedFlow()

    /**
     * Updates the UI state by applying a reduction function to the current state.
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = uiState.value.reduce()
        _uiState.value = newState
    }

    /**
     * Sends a one-time event to the UI.
     */
    protected fun setEvent(newEvent: Event) {
        viewModelScope.launch {
            _event.send(newEvent)
        }
    }

    /**
     * Emits an error to be handled by the UI.
     */
    protected fun setError(appError: AppError) {
        viewModelScope.launch {
            _error.emit(appError)
        }
    }
}