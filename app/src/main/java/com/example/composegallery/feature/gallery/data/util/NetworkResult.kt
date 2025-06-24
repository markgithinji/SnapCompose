package com.example.composegallery.feature.gallery.data.util

import kotlinx.coroutines.CancellationException
import java.io.IOException

inline fun <T> safeApiCall(block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e // Rethrow coroutine cancellation
    } catch (e: IOException) {
        Result.Error("No internet connection", e)
    } catch (e: retrofit2.HttpException) {
        Result.Error("HTTP ${e.code()}: ${e.message()}", e)
    } catch (e: Exception) {
        Result.Error("Unexpected error", e)
    }
}