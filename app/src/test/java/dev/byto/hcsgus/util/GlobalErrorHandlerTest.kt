package dev.byto.hcsgus.util

import android.database.sqlite.SQLiteException
import dev.byto.hcsgus.util.constant.HttpStatusCodes
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class GlobalErrorHandlerTest {

    @Test
    fun `mapToAppError given IOException returns NetworkError`() {
        val error = GlobalErrorHandler.mapToAppError(IOException())
        assertTrue(error is AppError.NetworkError)
        assertEquals("Network error. Please check your connection.", error.message)
    }

    @Test
    fun `mapToAppError given SocketTimeoutException returns NetworkError`() {
        val error = GlobalErrorHandler.mapToAppError(SocketTimeoutException())
        assertTrue(error is AppError.NetworkError)
        assertEquals("Network error. Please check your connection.", error.message)
    }

    @Test
    fun `mapToAppError given HttpException 404 returns HttpApiError NotFound`() {
        val httpException = HttpException(Response.error<Any>(HttpStatusCodes.NOT_FOUND, "".toResponseBody(null)))
        val error = GlobalErrorHandler.mapToAppError(httpException)
        assertTrue(error is AppError.HttpApiError)
        assertEquals("Resource not found.", error.message)
        assertEquals(HttpStatusCodes.NOT_FOUND, (error as AppError.HttpApiError).code)
    }

    @Test
    fun `mapToAppError given HttpException 422 returns HttpApiError UnprocessableEntity`() {
        val httpException = HttpException(Response.error<Any>(HttpStatusCodes.UNPROCESSABLE_ENTITY, "".toResponseBody(null)))
        val error = GlobalErrorHandler.mapToAppError(httpException)
        assertTrue(error is AppError.HttpApiError)
        assertEquals("Validation failed. Please check your input.", error.message)
        assertEquals(HttpStatusCodes.UNPROCESSABLE_ENTITY, (error as AppError.HttpApiError).code)
    }

    @Test
    fun `mapToAppError given HttpException 503 returns HttpApiError ServiceUnavailable`() {
        val httpException = HttpException(Response.error<Any>(HttpStatusCodes.SERVICE_UNAVAILABLE, "".toResponseBody(null)))
        val error = GlobalErrorHandler.mapToAppError(httpException)
        assertTrue(error is AppError.HttpApiError)
        assertEquals("Service is temporarily unavailable.", error.message)
        assertEquals(HttpStatusCodes.SERVICE_UNAVAILABLE, (error as AppError.HttpApiError).code)
    }

    @Test
    fun `mapToAppError given HttpException with other code returns HttpApiError ApiError`() {
        val otherHttpErrorCode = 500
        val httpException = HttpException(Response.error<Any>(otherHttpErrorCode, "".toResponseBody(null)))
        val error = GlobalErrorHandler.mapToAppError(httpException)
        assertTrue(error is AppError.HttpApiError)
        assertEquals("API error occurred.", error.message)
        assertEquals(otherHttpErrorCode, (error as AppError.HttpApiError).code)
    }

    @Test
    fun `mapToAppError given SQLiteException returns DatabaseError`() {
        val error = GlobalErrorHandler.mapToAppError(SQLiteException())
        assertTrue(error is AppError.DatabaseError)
        assertEquals("A local database error occurred.", error.message)
    }

    @Test
    fun `mapToAppError given other Throwable with message returns UnknownError`() {
        val customMessage = "Something went wrong"
        val error = GlobalErrorHandler.mapToAppError(RuntimeException(customMessage))
        assertTrue(error is AppError.UnknownError)
        assertEquals(customMessage, error.message)
    }

    @Test
    fun `mapToAppError given other Throwable with null message returns UnknownError with default message`() {
        val error = GlobalErrorHandler.mapToAppError(RuntimeException())
        assertTrue(error is AppError.UnknownError)
        assertEquals("An unexpected error happened.", error.message)
    }
}
