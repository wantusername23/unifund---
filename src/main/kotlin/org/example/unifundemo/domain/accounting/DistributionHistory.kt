package org.example.unifundemo.domain.accounting

import org.example.unifundemo.domain.worldview.WorldView
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class DistributionHistory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worldview_id", nullable = false)
    val worldview: WorldView,

    @Column(nullable = false)
    val totalAmount: BigDecimal,

    @Column(nullable = false)
    val distributionDate: LocalDateTime = LocalDateTime.now()
)