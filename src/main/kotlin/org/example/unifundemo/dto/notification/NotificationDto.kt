package org.example.unifundemo.dto.notification

import org.example.unifundemo.domain.notification.Notification
import java.time.LocalDateTime

data class NotificationResponse(
    val id: Long,
    val message: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(notification: Notification) = NotificationResponse(
            id = notification.id!!,
            message = notification.message,
            isRead = notification.isRead,
            createdAt = notification.createdAt
        )
    }
}