package org.example.unifundemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.http.HttpMethod
import org.springframework.security.config.http.SessionCreationPolicy // 💡 이 import 확인
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter // 💡 이 import 확인

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    // 비밀번호를 암호화하는 방법을 Spring에게 알려줌
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


    // HTTP 요청에 대한 보안 설정을 구성
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/users/signup", "/api/users/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/worldviews", "/api/worldviews/**").permitAll() // 💡 이 규칙에 GET /memberships가 포함됨
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
