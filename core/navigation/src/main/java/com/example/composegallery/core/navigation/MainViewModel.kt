package com.example.composegallery.core.navigation

import androidx.lifecycle.ViewModel
import com.example.composegallery.core.common.message.MessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val messageManager: MessageManager
) : ViewModel()
