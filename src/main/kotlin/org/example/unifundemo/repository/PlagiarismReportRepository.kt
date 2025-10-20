package org.example.unifundemo.repository

import org.example.unifundemo.domain.report.PlagiarismReport
import org.example.unifundemo.domain.report.PlagiarismReportType
import org.example.unifundemo.domain.report.ReportStatus
import org.springframework.data.jpa.repository.JpaRepository

interface PlagiarismReportRepository : JpaRepository<PlagiarismReport, Long> {
    // 사이트 관리자용 (세계관 표절 신고 목록)
    fun findByReportTypeAndStatus(type: PlagiarismReportType, status: ReportStatus): List<PlagiarismReport>

    // 세계관 창작자용 (자기 세계관의 게시글 표절 신고 목록)
    fun findByWorldviewIdAndReportTypeAndStatus(worldviewId: Long, type: PlagiarismReportType, status: ReportStatus): List<PlagiarismReport>
}