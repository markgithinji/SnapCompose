package com.example.composegallery.feature.gallery.data.util

import com.example.composegallery.R
import com.example.composegallery.feature.gallery.util.StringProvider
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

/**
 * Executes a given block of code, typically an API call, and wraps the result in a [Result] object.
 * This function handles common exceptions that might occur during the execution, such as network errors,
 * HTTP errors, and data parsing errors, providing localized error messages.
 *
 * It uses a [StringProvider] to fetch localized error messages.
 * Logs exceptions using Timber for debugging purposes.
 *
 * [CancellationException]s are re-thrown as they indicate that the coroutine executing the block
 * has been cancelled and should not be caught as a general error.
 *
 * @param T The type of the result expected from the block.
 * @param stringProvider An instance of [StringProvider] used to retrieve localized error messages.
 * @param block A lambda function representing the operation to be executed (e.g., an API call).
 *              This block is expected to return a value of type [T].
 * @return A [Result] object which is either:
 *         - [Result.Success] containing the successful result of type [T] from the [block].
 *         - [Result.Error] containing a localized error message and the original [Exception] if an error occurred.
 * @throws CancellationException if the coroutine executing the block is cancelled.
 */
inline fun <T> safeApiCall(stringProvider: StringProvider, block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        val message = stringProvider.get(R.string.error_no_internet_connection)
        Timber.tag("safeApiCall").e(e, "IO Exception: %s", message)
        Result.Error(message, e)
    } catch (e: HttpException) {
        val message = when (e.code()) {
            400 -> stringProvider.get(R.string.error_bad_request)
            401 -> stringProvider.get(R.string.error_unauthorized)
            403 -> stringProvider.get(R.string.error_forbidden)
            404 -> stringProvider.get(R.string.error_not_found)
            500 -> stringProvider.get(R.string.error_server_error)
            503 -> stringProvider.get(R.string.error_service_unavailable)
            else -> stringProvider.get(R.string.error_http_generic, e.code(), e.message())
        }
        Timber.tag("safeApiCall").e(e, "HTTP Exception: %s", message)
        Result.Error(message, e)
    } catch (e: IllegalStateException) {
        val message = stringProvider.get(R.string.error_invalid_data_received)
        Timber.tag("safeApiCall").e(e)
        Result.Error(message, e)
    } catch (e: IllegalArgumentException) {
        val message = stringProvider.get(R.string.error_unexpected_data_format)
        Timber.tag("safeApiCall").e(e)
        Result.Error(message, e)
    } catch (e: Exception) {
        val message = stringProvider.get(R.string.error_unexpected)
        Timber.tag("safeApiCall").e(e)
        Result.Error(message, e)
    }
}

/**
 * Wraps a database call in a try-catch block to handle potential exceptions.
 *
 * This function is designed to be used for executing database operations that might throw exceptions.
 * It ensures that any `CancellationException` is re-thrown, allowing for proper coroutine cancellation.
 * For other `Exception` types, it catches the exception, logs it using Timber,
 * and returns a `Result.Error` containing a generic error message retrieved from the `stringProvider`
 * and the original exception.
 * If the database call is successful, it returns a `Result.Success` with the result of the `block`.
 *
 * @param T The type of the result expected from the database call.
 * @param stringProvider An instance of [StringProvider] used to retrieve localized error messages.
 * @param block A lambda function representing the database call to be executed.
 *              This lambda should return a value of type `T`.
 * @return A [Result] object, which is either [Result.Success] containing the data of type `T`
 *         if the call was successful, or [Result.Error] containing an error message and the
 *         original exception if an error occurred.
 * @throws CancellationException if the underlying database call is cancelled.
 */
inline fun <T> safeDbCall(stringProvider: StringProvider, block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        val message = stringProvider.get(R.string.error_database)
        Timber.tag("safeDbCall").e(e)
        Result.Error(message, e)
    }
}