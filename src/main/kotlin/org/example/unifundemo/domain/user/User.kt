package org.example.unifundemo.domain.user

import jakarta.persistence.*
import java.math.BigDecimal
import org. example. unifundemo. domain. worldview. WorldView

@Entity
@Table(name = "users") // 데이터베이스 테이블 이름을 'users'로 지정
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,

    @Column(unique = true, nullable = false)
    var nickname: String,

    var balance: BigDecimal = BigDecimal.ZERO,

    var showBalance: Boolean = true,
    @Column(length = 500)
    var bio: String? = null, // 자기소개

    @Column
    var socialMediaLink: String? = null, // 소셜 미디어 링크

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representative_worldview_id")
    var representativeWorldview: WorldView? = null
)
