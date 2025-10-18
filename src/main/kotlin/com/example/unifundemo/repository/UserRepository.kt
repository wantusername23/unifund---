package com.example.unifundemo.repository

import com.example.unifundemo.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    // 이메일로 사용자를 찾는 기능을 추가
    fun findByEmail(email: String): User?
}
