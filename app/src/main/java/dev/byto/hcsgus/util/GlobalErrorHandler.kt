package dev.byto.hcsgus.util

import android.database.sqlite.SQLiteException
import dev.byto.hcsgus.util.constant.HttpStatusCodes
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Sealed class representing structured application errors.
 */
sealed class AppError(val message: String) {
    data class NetworkError(val customMessage: String = "Network error. Please check your connection.") :
        AppError(customMessage)

    data class HttpApiError(val customMessage: String, val code: Int) : AppError(customMessage)
    data class DatabaseError(val customMessage: String = "A local database error occurred.") :
        AppError(customMessage)

    data class UnknownError(val customMessage: String = "An unknown error occurred.") :
        AppError(customMessage)
}

/**
 * A global error handler to convert Throwables into structured AppError types.
 */
object GlobalErrorHandler {
    fun mapToAppError(throwable: Throwable): AppError {
        return when (throwable) {
            is IOException, is SocketTimeoutException -> AppError.NetworkError()
            is HttpException -> {
                val message = when (throwable.code()) {
                    HttpStatusCodes.NOT_FOUND -> "Resource not found."
                    HttpStatusCodes.UNPROCESSABLE_ENTITY -> "Validation failed. Please check your input."
                    HttpStatusCodes.SERVICE_UNAVAILABLE -> "Service is temporarily unavailable."
                    else -> "API error occurred."
                }
                AppError.HttpApiError(message, throwable.code())
            }
            // Can add more specific database exceptions here
             is SQLiteException -> AppError.DatabaseError()
            else -> AppError.UnknownError(throwable.message ?: "An unexpected error happened.")
        }
    }
}