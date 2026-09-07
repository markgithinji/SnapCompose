package com.example.composegallery.core.common.network.di

import com.example.composegallery.core.common.network.ConnectivityManagerNetworkMonitor
import com.example.composegallery.core.common.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkMonitorModule {
    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        connectivityManagerNetworkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}
