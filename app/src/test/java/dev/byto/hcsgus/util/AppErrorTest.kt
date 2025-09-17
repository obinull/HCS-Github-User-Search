package dev.byto.hcsgus.util

import org.junit.Assert.assertEquals
import org.junit.Test

class AppErrorTest {

    @Test
    fun `NetworkError with default message`() {
        val error = AppError.NetworkError()
        assertEquals("Network error. Please check your connection.", error.message)
        assertEquals("Network error. Please check your connection.", error.customMessage)
    }

    @Test
    fun `NetworkError with custom message`() {
        val customMsg = "Custom network issue"
        val error = AppError.NetworkError(customMessage = customMsg)
        assertEquals(customMsg, error.message)
        assertEquals(customMsg, error.customMessage)
    }

    @Test
    fun `HttpApiError sets message and code`() {
        val customMsg = "API resource unavailable"
        val errorCode = 404
        val error = AppError.HttpApiError(customMessage = customMsg, code = errorCode)
        assertEquals(customMsg, error.message)
        assertEquals(customMsg, error.customMessage)
        assertEquals(errorCode, error.code)
    }

    @Test
    fun `DatabaseError with default message`() {
        val error = AppError.DatabaseError()
        assertEquals("A local database error occurred.", error.message)
        assertEquals("A local database error occurred.", error.customMessage)
    }

    @Test
    fun `DatabaseError with custom message`() {
        val customMsg = "Custom database issue"
        val error = AppError.DatabaseError(customMessage = customMsg)
        assertEquals(customMsg, error.message)
        assertEquals(customMsg, error.customMessage)
    }

    @Test
    fun `UnknownError with default message`() {
        val error = AppError.UnknownError()
        assertEquals("An unknown error occurred.", error.message)
        assertEquals("An unknown error occurred.", error.customMessage)
    }

    @Test
    fun `UnknownError with custom message`() {
        val customMsg = "A very specific unknown problem"
        val error = AppError.UnknownError(customMessage = customMsg)
        assertEquals(customMsg, error.message)
        assertEquals(customMsg, error.customMessage)
    }
}
