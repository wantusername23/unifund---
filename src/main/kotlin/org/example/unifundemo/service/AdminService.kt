package org.example.unifundemo.service

import org.example.unifundemo.domain.report.PlagiarismReport
import org.example.unifundemo.domain.report.PlagiarismReportType
import org.example.unifundemo.dto.report.CreateReportRequest
import org.example.unifundemo.repository.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.example.unifundemo.domain.notification.Notification
import org.example.unifundemo.repository.NotificationRepository
import org. example. unifundemo. domain. report. ReportStatus
import org. example. unifundemo. domain. post. Post
import org. example. unifundemo. domain. post. BoardType
import org. example. unifundemo. domain. post. PostStatus
import org.springframework.security.access.AccessDeniedException


@Service
@Transactional
class AdminService(
    private val reportRepository: PlagiarismReportRepository,
    private val userRepository: UserRepository,
    private val worldviewRepository: WorldViewRepository,
    private val postRepository: PostRepository,
    private val notificationRepository: NotificationRepository
) {
    fun createReport(userEmail: String, request: CreateReportRequest) {
        val reporter = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val worldview = worldviewRepository.findById(request.worldviewId).orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        val report = if (request.postId != null) { // 게시글 신고
            val post = postRepository.findById(request.postId).orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }
            // ✅ named arguments를 사용하여 오류 수정
            PlagiarismReport(
                reporter = reporter,
                reportType = PlagiarismReportType.POST,
                worldview = worldview,
                post = post,
                reason = request.reason
            )
        } else { // 세계관 신고
            // ✅ named arguments를 사용하여 오류 수정
            PlagiarismReport(
                reporter = reporter,
                reportType = PlagiarismReportType.WORLDVIEW,
                worldview = worldview,
                reason = request.reason
            )
        }
        reportRepository.save(report)
    }
    fun resolvePostPlagiarism(reportId: Long, decision: ReportStatus, creatorEmail: String) {
        val report = reportRepository.findById(reportId).orElseThrow { EntityNotFoundException("신고를 찾을 수 없습니다.") }

        // 권한 검사
        if (report.worldview.creator.email != creatorEmail) {
            throw AccessDeniedException("이 신고를 처리할 권한이 없습니다.")
        }

        report.status = decision
        if (decision == ReportStatus.RESOLVED) {
            val plagiarizedPost = report.post!!

            // 자동 공지 생성
            val noticeContent = "'${plagiarizedPost.title}' 게시글이 표절로 확인되어 삭제 조치되었습니다."
            val notice = Post(
                title = "[공지] 게시글 삭제 안내",
                content = noticeContent,
                boardType = BoardType.FREE,
                author = report.worldview.creator,
                worldview = report.worldview,
                status = PostStatus.APPROVED,
                isNotice = true
            )
            postRepository.save(notice)

            // 표절 게시글 삭제
            postRepository.delete(plagiarizedPost)
        }
        reportRepository.save(report)
    }

    // ✅ 세계관 표절 신고 처리 (사이트 관리자용)
    fun resolveWorldviewPlagiarism(reportId: Long, decision: ReportStatus) {
        // 💡 실제 서비스에서는 여기에 사이트 관리자(ADMIN) 역할(Role) 검증 로직이 필요합니다.

        val report = reportRepository.findById(reportId).orElseThrow { EntityNotFoundException("신고를 찾을 수 없습니다.") }
        report.status = decision

        if (decision == ReportStatus.RESOLVED) {
            val plagiarizedWorldview = report.worldview
            val violator = plagiarizedWorldview.creator

            // 경고 메시지 생성 및 저장
            val warningMessage = "귀하의 세계관 '${plagiarizedWorldview.name}'은(는) 표절로 확인되어 삭제되었으며, 서비스 이용에 제한이 있을 수 있습니다."
            val notification = Notification(user = violator, message = warningMessage)
            notificationRepository.save(notification)

            // 세계관 및 관련 데이터 삭제 (주의: 실제 서비스에서는 데이터 백업 등 정책 필요)
            worldviewRepository.delete(plagiarizedWorldview)
        }
        reportRepository.save(report)
    }
}