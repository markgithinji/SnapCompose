package com.example.composegallery.core.common.message

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageManager @Inject constructor() {
    private val _messages = MutableStateFlow<List<UserMessage>>(emptyList())
    val messages: StateFlow<List<UserMessage>> = _messages.asStateFlow()

    fun showMessage(
        message: String,
        actionLabel: String? = null,
        duration: MessageDuration = MessageDuration.Short
    ) {
        _messages.update { currentMessages ->
            currentMessages + UserMessage(message, actionLabel, duration)
        }
    }

    fun messageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}
