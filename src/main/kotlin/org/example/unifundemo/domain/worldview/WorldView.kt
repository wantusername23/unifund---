package org.example.unifundemo.domain.worldview

import org.example.unifundemo.domain.user.User
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class WorldView(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Lob
    @Column(columnDefinition = "TEXT")
    var description: String,

    var keywords: String, // 간단하게 콤마(,)로 구분된 문자열로 저장

    var coverImageUrl: String,

    // 이 세계관을 만든 창조자(User) 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    val creator: User,
    var revenuePool: BigDecimal = BigDecimal.ZERO
)