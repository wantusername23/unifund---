package org.example.unifundemo.service

import org.example.unifundemo.config.JwtTokenProvider
import org.example.unifundemo.domain.user.User
import org.example.unifundemo.dto.user.LoginRequest
import org.example.unifundemo.dto.user.SignUpRequest

import org.example.unifundemo.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional // 데이터베이스 작업을 하나의 단위로 묶어줌
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: org.example.unifundemo.config.JwtTokenProvider // JwtTokenProvider 주입
) {

    fun signUp(request: SignUpRequest): User {
            // 1. 이메일 중복 체크
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("이미 사용 중인 이메일입니다.")
        }

            // 2. 비밀번호를 암호화
        val encodedPassword = passwordEncoder.encode(request.password)

            // 3. User 객체를 만들어서 데이터베이스에 저장
        val user = User(
            email = request.email,
            passwordHash = encodedPassword,
            nickname = request.nickname
        )
        return userRepository.save(user)
    }


    // 로그인 메소드 추가
    fun login(request: LoginRequest): String {
        // 1. 이메일로 사용자 조회
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("가입되지 않은 이메일입니다.")

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw IllegalArgumentException("잘못된 비밀번호입니다.")
        }

        // 3. 비밀번호가 일치하면 JWT 생성하여 반환
        return jwtTokenProvider.generateToken(user.email)
    }
}
