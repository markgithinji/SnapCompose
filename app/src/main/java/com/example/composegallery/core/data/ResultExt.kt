package com.example.composegallery.core.data

import com.example.composegallery.R
import com.example.composegallery.core.util.StringProvider
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection

inline fun <T> safeApiCall(stringProvider: StringProvider, block: () -> T): Result<T> {
    Timber.tag("safeApiCall").d("Executing block")
    return try {
        val result = block()
        Timber.tag("safeApiCall").d("Success")
        Result.Success(result)
    } catch (e: CancellationException) {
        Timber.tag("safeApiCall").d("Cancelled")
        throw e
    } catch (e: IOException) {
        val message = stringProvider.get(R.string.error_no_internet_connection)
        Timber.tag("safeApiCall").e(e, "IO Exception: %s", message)
        Result.Error(message, AppException(message, e))
    } catch (e: HttpException) {
        val message = when (e.code()) {
            HttpURLConnection.HTTP_BAD_REQUEST -> stringProvider.get(R.string.error_bad_request)
            HttpURLConnection.HTTP_UNAUTHORIZED -> stringProvider.get(R.string.error_unauthorized)
            HttpURLConnection.HTTP_FORBIDDEN -> stringProvider.get(R.string.error_forbidden)
            HttpURLConnection.HTTP_NOT_FOUND -> stringProvider.get(R.string.error_not_found)
            HttpURLConnection.HTTP_INTERNAL_ERROR -> stringProvider.get(R.string.error_server_error)
            HttpURLConnection.HTTP_UNAVAILABLE -> stringProvider.get(R.string.error_service_unavailable)
            else -> stringProvider.get(R.string.error_http_generic, e.code())
        }
        val errorBody = e.response()?.errorBody()?.string()
        Timber.tag("safeApiCall").e(e, "HTTP Exception [%d]: %s. Body: %s", e.code(), message, errorBody)
        Result.Error(message, AppException(message, e))
    } catch (e: IllegalStateException) {
        val message = stringProvider.get(R.string.error_invalid_data_received)
        Timber.tag("safeApiCall").e(e)
        Result.Error(message, AppException(message, e))
    } catch (e: IllegalArgumentException) {
        val message = stringProvider.get(R.string.error_unexpected_data_format)
        Timber.tag("safeApiCall").e(e)
        Result.Error(message, AppException(message, e))
    } catch (e: Exception) {
        val message = stringProvider.get(R.string.error_unexpected)
        Timber.tag("safeApiCall").e(e)
        Result.Error(message, AppException(message, e))
    }
}

inline fun <T> safeDbCall(stringProvider: StringProvider, block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        val message = stringProvider.get(R.string.error_database)
        Timber.tag("safeDbCall").e(e)
        Result.Error(message, AppException(message, e))
    }
}
