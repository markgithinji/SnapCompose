package com.example.composegallery.feature.gallery.data.util

/**
 * Custom exception class for the application to handle and display user-friendly error messages.
 *
 * @param message The localized, human-readable error message to be displayed to the user.
 * @param cause The underlying exception that triggered this error (optional).
 */
class AppException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause)
