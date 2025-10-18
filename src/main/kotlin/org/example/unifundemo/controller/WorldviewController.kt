package org.example.unifundemo.controller

import org.example.unifundemo.dto.worldview.CreateWorldviewRequest
import org.example.unifundemo.service.WorldviewService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.security.Principal
import org.example.unifundemo.dto.worldview.WorldviewDetailResponse
import org.example.unifundemo.dto.worldview.WorldviewSimpleResponse
import org.example.unifundemo.dto.membership.CreateMembershipRequest
import org.example.unifundemo.dto.membership.MembershipResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/worldviews")
class WorldviewController(
    private val worldviewService: WorldviewService
) {
    @PostMapping
    fun createWorldview(
        principal: Principal, // 💡 JWT 필터가 인증 정보를 넣어주면, 여기서 현재 로그인한 사용자의 이메일을 꺼낼 수 있습니다.
        @Valid @RequestBody request: CreateWorldviewRequest
    ): ResponseEntity<String> {
        val userEmail = principal.name
        worldviewService.createWorldview(userEmail, request)
        return ResponseEntity.status(HttpStatus.CREATED).body("세계관이 성공적으로 생성되었습니다.")
    }
    @GetMapping
    fun getAllWorldviews(): ResponseEntity<List<WorldviewSimpleResponse>> {
        val worldviews = worldviewService.getAllWorldviews()
        return ResponseEntity.ok(worldviews)
    }

    // 특정 세계관 상세 조회 API 추가
    @GetMapping("/{id}")
    fun getWorldviewById(@PathVariable id: Long): ResponseEntity<WorldviewDetailResponse> {
        val worldview = worldviewService.getWorldviewById(id)
        return ResponseEntity.ok(worldview)
    }
    @PostMapping("/{worldviewId}/memberships")
    fun addMembershipTier(
        @PathVariable worldviewId: Long,
        principal: Principal,
        @Valid @RequestBody request: CreateMembershipRequest
    ): ResponseEntity<MembershipResponse> {
        val userEmail = principal.name
        val membershipResponse = worldviewService.addMembershipTier(worldviewId, userEmail, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipResponse)
    }
    @GetMapping("/{worldviewId}/memberships")
    fun getMembershipTiers(@PathVariable worldviewId: Long): ResponseEntity<List<MembershipResponse>> {
        val memberships = worldviewService.getMembershipTiers(worldviewId)
        return ResponseEntity.ok(memberships)
    }
    // 멤버십 가입(구독) API
    @PostMapping("/memberships/{membershipId}/subscribe")
    fun subscribeToMembership(
        @PathVariable membershipId: Long,
        principal: Principal
    ): ResponseEntity<String> {
        val userEmail = principal.name
        worldviewService.subscribeToMembership(membershipId, userEmail)
        return ResponseEntity.ok("멤버십 가입이 완료되었습니다.")
    }
    @GetMapping("/search")
    fun searchWorldviews(@RequestParam q: String): ResponseEntity<List<WorldviewSimpleResponse>> {
        val worldviews = worldviewService.searchWorldviews(q)
        return ResponseEntity.ok(worldviews)
    }
}