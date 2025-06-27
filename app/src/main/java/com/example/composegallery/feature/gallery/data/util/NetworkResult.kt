package com.example.composegallery.feature.gallery.data.util

import kotlinx.coroutines.CancellationException
import timber.log.Timber
import java.io.IOException

inline fun <T> safeApiCall(block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e // Don't swallow coroutine cancellation
    } catch (e: IOException) {
        Timber.tag("safeApiCall").e(e, "IO Exception: No internet connection")
        Result.Error("No internet connection", e)
    } catch (e: retrofit2.HttpException) {
        val message = when (e.code()) {
            400 -> "Bad request. Something is wrong with the request."
            401 -> "Unauthorized. Please check your access token."
            403 -> "Forbidden. You don't have permission to access this resource."
            404 -> "Not found. The resource you're looking for doesn't exist."
            500 -> "Server error. Something went wrong on Unsplash's end."
            503 -> "Service unavailable. Try again later."
            else -> "HTTP ${e.code()}: ${e.message()}"
        }
        Timber.tag("safeApiCall").e(e, "HTTP Exception: $message")
        Result.Error(message, e)
    } catch (e: Exception) {
        Timber.tag("safeApiCall").e(e, "Unexpected error")
        Result.Error("Unexpected error", e)
    }
}
