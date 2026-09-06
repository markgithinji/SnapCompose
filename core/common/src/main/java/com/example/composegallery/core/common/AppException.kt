package com.example.composegallery.core.common

class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
