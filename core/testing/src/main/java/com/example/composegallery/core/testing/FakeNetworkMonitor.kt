package com.example.composegallery.core.testing

import com.example.composegallery.core.common.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor : NetworkMonitor {
    private val isOnlineFlow = MutableStateFlow(true)

    override val isOnline: Flow<Boolean> = isOnlineFlow

    fun setOnline(online: Boolean) {
        isOnlineFlow.value = online
    }
}
