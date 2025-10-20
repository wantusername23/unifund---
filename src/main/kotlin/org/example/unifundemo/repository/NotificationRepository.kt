package org.example.unifundemo.repository

import org.example.unifundemo.domain.notification.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.example.unifundemo.domain.user.User

interface NotificationRepository : JpaRepository<Notification, Long>{
    // ✅ 아래 메소드 추가
    fun findByUserOrderByIdDesc(user: User): List<Notification>
}