package org.example.unifundemo.controller

import org.example.unifundemo.domain.report.ReportStatus
import org.example.unifundemo.dto.report.CreateReportRequest
import org.example.unifundemo.service.AdminService // ReportService -> AdminService
import org.example.unifundemo.service.WorldviewService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import org.example.unifundemo.domain.accounting.DistributionHistory
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService,
    private val worldviewService: WorldviewService
) {
    // 신고 접수
    @PostMapping("/reports")
    fun createReport(principal: Principal, @RequestBody request: CreateReportRequest): ResponseEntity<String> {
        // ... (기존 ReportController의 내용과 동일)
        adminService.createReport(principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body("신고가 접수되었습니다.")
    }

    // 게시글 표절 신고 처리
    @PostMapping("/reports/post/{reportId}")
    fun resolvePostReport(
        @PathVariable reportId: Long,
        @RequestParam decision: ReportStatus,
        principal: Principal
    ): ResponseEntity<String> {
        adminService.resolvePostPlagiarism(reportId, decision, principal.name)
        return ResponseEntity.ok("게시글 신고가 처리되었습니다.")
    }

    // 세계관 표절 신고 처리
    @PostMapping("/reports/worldview/{reportId}")
    fun resolveWorldviewReport(
        @PathVariable reportId: Long,
        @RequestParam decision: ReportStatus
    ): ResponseEntity<String> {
        // 💡 실제로는 @PreAuthorize("hasRole('ADMIN')") 등으로 사이트 관리자만 접근 가능하게 해야 함
        adminService.resolveWorldviewPlagiarism(reportId, decision)
        return ResponseEntity.ok("세계관 신고가 처리되었습니다.")
    }
    // 특정 세계관의 관리자 정보(수익 포함) 조회
    @GetMapping("/worldviews/{id}")
    fun getWorldviewForAdmin(
        @PathVariable id: Long,
        principal: Principal
    ): ResponseEntity<Any> { // DTO를 만들거나 Map을 사용할 수 있습니다.
        val worldview = worldviewService.getWorldviewForAdmin(id, principal.name)
        return ResponseEntity.ok(worldview)
    }
    @GetMapping("/worldviews/{id}/history")
    fun getDistributionHistory(
        @PathVariable id: Long,
        principal: Principal
    ): ResponseEntity<List<DistributionHistory>> {
        val history = worldviewService.getDistributionHistory(id, principal.name)
        return ResponseEntity.ok(history)
    }

    // 수익 분배 실행
    @PostMapping("/worldviews/{id}/distribute")
    fun distributeRevenue(
        @PathVariable id: Long,
        principal: Principal
    ): ResponseEntity<String> {
        worldviewService.distributeRevenue(id, principal.name)
        return ResponseEntity.ok("수익 분배가 시작되었습니다.")
    }
}