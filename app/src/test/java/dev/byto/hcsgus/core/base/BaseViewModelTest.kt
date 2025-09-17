package dev.byto.hcsgus.core.base

import app.cash.turbine.test
import dev.byto.hcsgus.util.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class BaseViewModelTest {

    // 1. Define simple State and Event types for testing
    private data class TestState(val count: Int = 0, val message: String = "Initial")
    private sealed class TestEvent {
        data object SampleEvent : TestEvent()
        data class DataEvent(val data: String) : TestEvent()
    }

    private val initialTestState = TestState(count = 0, message = "Initial")

    // 2. Create a concrete implementation of BaseViewModel for testing
    private class ConcreteViewModel(initialState: TestState) :
        BaseViewModel<TestState, TestEvent>() {
        private val _initialState = initialState
        override fun createInitialState(): TestState = _initialState

        // Expose protected methods for easier testing if needed, or test via public flows
        public override fun setState(reduce: TestState.() -> TestState) {
            super.setState(reduce)
        }

        public override fun setEvent(newEvent: TestEvent) {
            super.setEvent(newEvent)
        }

        public override fun setError(appError: AppError) {
            super.setError(appError)
        }
    }

    private lateinit var viewModel: ConcreteViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ConcreteViewModel(initialTestState)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialState is set correctly and emitted by uiState`() = runTest {
        viewModel.uiState.test {
            assertEquals(initialTestState, awaitItem())
            expectNoEvents() // Ensure no other initial emissions
        }
    }

    @Test
    fun `setState updates uiState with new state`() = runTest {
        val newState = TestState(count = 1, message = "Updated")

        viewModel.uiState.test {
            assertEquals(initialTestState, awaitItem()) // Consume initial state

            viewModel.setState { newState }

            assertEquals(newState, awaitItem()) // Assert new state
            expectNoEvents()
        }
    }

    @Test
    fun `setState lambda operates on current state`() = runTest {
        viewModel.uiState.test {
            assertEquals(initialTestState, awaitItem()) // Initial: count = 0

            viewModel.setState { copy(count = this.count + 1) } // count becomes 1
            assertEquals(TestState(count = 1, message = "Initial"), awaitItem())

            viewModel.setState { copy(message = "Second Update", count = this.count * 2) } // count becomes 2
            assertEquals(TestState(count = 2, message = "Second Update"), awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `setEvent sends event correctly through event Flow`() = runTest {
        val eventToSend = TestEvent.SampleEvent

        viewModel.event.test {
            viewModel.setEvent(eventToSend)
            assertEquals(eventToSend, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `setEvent sends multiple events correctly`() = runTest {
        val event1 = TestEvent.SampleEvent
        val event2 = TestEvent.DataEvent("payload")

        viewModel.event.test {
            viewModel.setEvent(event1)
            assertEquals(event1, awaitItem())

            viewModel.setEvent(event2)
            assertEquals(event2, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `setError emits AppError correctly through error SharedFlow`() = runTest {
        val errorToEmit = AppError.NetworkError("Test network error")

        viewModel.error.test {
            viewModel.setError(errorToEmit)
            assertEquals(errorToEmit, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `setError emits multiple AppErrors correctly`() = runTest {
        val error1 = AppError.UnknownError("Unknown test error")
        val error2 = AppError.HttpApiError("API error", 404)

        viewModel.error.test {
            viewModel.setError(error1)
            assertEquals(error1, awaitItem())

            viewModel.setError(error2)
            assertEquals(error2, awaitItem())

            expectNoEvents()
        }
    }
}
