package org.example.unifundemo.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: org.example.unifundemo.config.JwtTokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1. 요청 헤더에서 토큰을 꺼냅니다.
        val token = resolveToken(request)

        // 2. 토큰이 유효한지 검사합니다.
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 3. 토큰이 유효하면, 토큰에서 사용자 정보를 받아옵니다.
            val email = jwtTokenProvider.getEmailFromToken(token)
            // 4. SecurityContext에 인증 정보를 저장합니다.
            //    이렇게 저장해두면, 해당 요청을 처리하는 동안 이 사용자는 '인증된 사용자'로 간주됩니다.
            val authentication = UsernamePasswordAuthenticationToken(email, null, emptyList())
            SecurityContextHolder.getContext().authentication = authentication
        }

        // 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response)
    }

    // HTTP 요청 헤더에서 "Bearer " 접두사를 제거하고 실제 토큰만 추출하는 메소드
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }
}