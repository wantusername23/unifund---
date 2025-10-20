package org.example.unifundemo.controller//package org.example.unifundemo.controller
//
//import org.example.unifundemo.dto.report.CreateReportRequest
//import org.example.unifundemo.service.AdminService
//import jakarta.validation.Valid
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//import java.security.Principal
//
//@RestController
//@RequestMapping("/api/reports")
//class ReportController(
//    private val adminService: AdminService
//) {
//    @PostMapping
//    fun createReport(principal: Principal, @Valid @RequestBody request: CreateReportRequest): ResponseEntity<String> {
//        adminService.createReport(principal.name, request)
//        return ResponseEntity.status(HttpStatus.CREATED).body("신고가 접수되었습니다.")
//    }
//}