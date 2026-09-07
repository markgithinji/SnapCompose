package com.example.composegallery.core.common.network

import kotlinx.coroutines.flow.Flow

/**
 * Utility for reporting of network connectivity status.
 */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
