package org.example.unifundemo.domain.user

import jakarta.persistence.*
import java.math.BigDecimal

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

    var balance: BigDecimal = BigDecimal.ZERO
)
