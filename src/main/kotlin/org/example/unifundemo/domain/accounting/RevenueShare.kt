package org.example.unifundemo.domain.accounting

import org.example.unifundemo.domain.user.User
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class RevenueShare(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    val history: DistributionHistory,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val amount: BigDecimal,

    @Column(nullable = false)
    val description: String // 예: "창작자 수익", "인기글 보상"
)