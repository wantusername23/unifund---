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
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import org.example.unifundemo.dto.worldview.ContributorRequest
import org.example.unifundemo.dto.worldview.ContributorResponse
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/api/worldviews")
class WorldviewController(
    private val worldviewService: WorldviewService
) {
    // ✅ 1. 파일 업로드용 엔드포인트 경로 지정
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createWorldviewWithUpload(
        principal: Principal,
        @RequestPart("request") @Valid request: CreateWorldviewRequest,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<String> {
        val userEmail = principal.name
        worldviewService.createWorldview(userEmail, request, file)
        return ResponseEntity.status(HttpStatus.CREATED).body("세계관이 성공적으로 생성되었습니다.")
    }

    // ✅ 2. AI 이미지 URL용 엔드포인트 추가
    @PostMapping("/url")
    fun createWorldviewWithUrl(
        principal: Principal,
        @Valid @RequestBody request: CreateWorldviewRequest
    ): ResponseEntity<String> {
        val userEmail = principal.name
        worldviewService.createWorldview(userEmail, request)
        return ResponseEntity.status(HttpStatus.CREATED).body("세계관이 성공적으로 생성되었습니다.")
    }
    @GetMapping
    fun getAllWorldviews(principal: Principal?): ResponseEntity<List<WorldviewSimpleResponse>> { // ✅ Principal? 추가
        // ✅ principal?.name (로그인한 사용자의 이메일) 전달
        val worldviews = worldviewService.getAllWorldviews(principal?.name)
        return ResponseEntity.ok(worldviews)
    }

    @GetMapping("/{id}")
    fun getWorldviewById(
        @PathVariable id: Long,
        principal: Principal? // ✅ Principal? 추가
    ): ResponseEntity<WorldviewDetailResponse> {
        // ✅ principal?.name 전달
        val worldview = worldviewService.getWorldviewById(id, principal?.name)
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
    fun searchWorldviews(@RequestParam q: String, principal: Principal?): ResponseEntity<List<WorldviewSimpleResponse>> {
        val worldviews = worldviewService.searchWorldviews(q, principal?.name)
        return ResponseEntity.ok(worldviews)
    }
    @PostMapping("/{worldviewId}/contributors")
    fun addContributor(
        @PathVariable worldviewId: Long,
        principal: Principal,
        @RequestBody request: ContributorRequest
    ): ResponseEntity<ContributorResponse> {
        val contributor = worldviewService.addContributor(worldviewId, principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(contributor)
    }

    @GetMapping("/{worldviewId}/contributors")
    fun getContributors(@PathVariable worldviewId: Long): ResponseEntity<List<ContributorResponse>> {
        val contributors = worldviewService.getContributors(worldviewId)
        return ResponseEntity.ok(contributors)
    }
    @GetMapping("/search/by-tag")
    fun searchWorldviewsByTag(@RequestParam tag: String, principal: Principal?): ResponseEntity<List<WorldviewSimpleResponse>> {
        val worldviews = worldviewService.findWorldviewsByTag(tag, principal?.name)
        return ResponseEntity.ok(worldviews)
    }
}