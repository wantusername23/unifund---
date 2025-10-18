package com.example.unifundemo.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// 회원가입 요청 DTO
data class SignUpRequest(
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:NotBlank
    val email: String,

    @field:Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @field:NotBlank
    val password: String,

    @field:NotBlank(message = "닉네임은 비워둘 수 없습니다.")
    val nickname: String
)

// 로그인 요청 DTO
data class LoginRequest(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val password: String
)
