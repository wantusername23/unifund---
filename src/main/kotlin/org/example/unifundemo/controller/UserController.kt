package com.example.unifundemo.controller

import com.example.unifundemo.dto.user.SignUpRequest
import com.example.unifundemo.dto.user.LoginRequest
import com.example.unifundemo.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users") // 이 컨트롤러의 모든 API 주소는 /api/users로 시작
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody request: SignUpRequest): ResponseEntity<String> {
        userService.signUp(request)
        // 성공 시 201 Created 상태와 메시지 반환
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.")
    }

    // TODO: 로그인 API 구현
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<String> {
        val token = userService.login(request)
        // 성공 시 200 OK 상태와 함께 발급된 토큰(출입증)을 반환
        return ResponseEntity.ok(token)
    }
}
