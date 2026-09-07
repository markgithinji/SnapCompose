package com.example.composegallery.core.common.message

import java.util.UUID

data class UserMessage(
    val message: String,
    val actionLabel: String? = null,
    val duration: MessageDuration = MessageDuration.Short,
    val id: Long = UUID.randomUUID().mostSignificantBits
)

enum class MessageDuration {
    Short, Long, Indefinite
}
