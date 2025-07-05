package com.example.composegallery.feature.gallery.data.util

import com.example.composegallery.R
import com.example.composegallery.feature.gallery.util.StringProvider
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

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