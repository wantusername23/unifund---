package org.example.unifundemo.domain.membership

import org.example.unifundemo.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class UserMembership(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id")
    val membership: Membership,

    @Column(nullable = false)
    val subscriptionDate: LocalDateTime = LocalDateTime.now()
)