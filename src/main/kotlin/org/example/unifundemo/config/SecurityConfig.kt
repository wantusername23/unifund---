package com.example.unifundemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    // 비밀번호를 암호화하는 방법을 Spring에게 알려줌
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // HTTP 요청에 대한 보안 설정을 구성
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // CSRF 보호 비활성화 (API 서버에서는 보통 비활성화)
            .authorizeHttpRequests {
                // 이 주소들로 오는 요청은 인증 없이 허용
                it.requestMatchers("/api/users/signup", "/api/users/login").permitAll()
                    // 그 외 모든 요청은 인증이 필요함
                    .anyRequest().authenticated()
            }
        return http.build()
    }
}
