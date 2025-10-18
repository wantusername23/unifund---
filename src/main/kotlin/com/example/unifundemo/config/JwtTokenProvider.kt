package com.example.unifundemo.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretString: String,
    @Value("\${jwt.expiration-hours}") private val expirationHours: Long
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretString.toByteArray())

    // JWT(출입증) 생성
    fun generateToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationHours * 3600 * 1000)

        return Jwts.builder()
            .setSubject(email) // 토큰의 주체 (누구의 것인지)
            .setIssuedAt(now) // 발급 시간
            .setExpiration(expiryDate) // 만료 시간
            .signWith(secretKey, SignatureAlgorithm.HS512) // 비밀키로 서명
            .compact()
    }

    // JWT(출입증)에서 이메일 정보 추출
    fun getEmailFromToken(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    // JWT(출입증) 유효성 검증
    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            // 만료, 형식 오류 등 토큰이 유효하지 않은 경우
            return false
        }
    }
}
