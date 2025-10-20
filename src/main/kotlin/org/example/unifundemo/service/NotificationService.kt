package org.example.unifundemo.service

import org.example.unifundemo.dto.notification.NotificationResponse
import org.example.unifundemo.repository.NotificationRepository
import org.example.unifundemo.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun getMyNotifications(userEmail: String): List<NotificationResponse> {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        return notificationRepository.findByUserOrderByIdDesc(user).map { NotificationResponse.from(it) }
    }

    fun markAsRead(notificationId: Long, userEmail: String) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { EntityNotFoundException("알림을 찾을 수 없습니다.") }

        if (notification.user.email != userEmail) {
            throw AccessDeniedException("이 알림을 읽음 처리할 권한이 없습니다.")
        }
        notification.isRead = true
        notificationRepository.save(notification)
    }
}