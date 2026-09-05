package com.example.composegallery.core.data

class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
