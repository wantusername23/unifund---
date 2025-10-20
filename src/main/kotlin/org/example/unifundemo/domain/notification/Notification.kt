package org.example.unifundemo.domain.notification

import org.example.unifundemo.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Notification(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User, // 알림을 받을 사용자

    @Column(nullable = false, length = 500)
    val message: String,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)