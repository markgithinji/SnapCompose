package com.example.composegallery.core.network

class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
