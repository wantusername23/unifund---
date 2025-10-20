package org.example.unifundemo.domain.worldview

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class TimelineEvent(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String, // 사건명

    @Lob
    @Column(columnDefinition = "TEXT")
    var description: String, // 사건에 대한 설명

    @Column(nullable = false)
    var eventDate: String, // 사건 발생 시점 (e.g., "B.C. 100년", "전쟁 발발 3일차")

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_view_id")
    val worldView: WorldView
)