package org.example.unifundemo.domain.membership

import org.example.unifundemo.domain.worldview.WorldView
import jakarta.persistence.*

@Entity
class Membership(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String, // 예: "후원자", "스토리 참여자"

    @Column(nullable = false)
    var price: Int, // 월 구독 가격

    @Column(nullable = false)
    var description: String, // 멤버십 혜택 설명

    @Column(nullable = false)
    var level: Int, // 등급 순서 (예: 1, 2, 3)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worldview_id", nullable = false)
    val worldview: WorldView // 이 멤버십이 속한 세계관
)