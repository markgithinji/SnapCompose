package com.example.composegallery.core.common

import com.example.composegallery.core.common.R
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

inline fun <T> safeApiCall(stringProvider: StringProvider, block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        val message = stringProvider.get(R.string.error_no_internet_connection)
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
        Result.Error(message, AppException(message, e))
    } catch (e: IllegalStateException) {
        val message = stringProvider.get(R.string.error_invalid_data_received)
        Result.Error(message, AppException(message, e))
    } catch (e: IllegalArgumentException) {
        val message = stringProvider.get(R.string.error_unexpected_data_format)
        Result.Error(message, AppException(message, e))
    } catch (e: Exception) {
        val message = stringProvider.get(R.string.error_unexpected)
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
        Result.Error(message, AppException(message, e))
    }
}
