package org.example.unifundemo.domain.report

import org.example.unifundemo.domain.post.Post
import org.example.unifundemo.domain.user.User
import org.example.unifundemo.domain.worldview.WorldView
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class PlagiarismReport(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    val reporter: User, // 신고자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val reportType: PlagiarismReportType, // 신고 유형

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worldview_id", nullable = false)
    val worldview: WorldView, // 관련 세계관

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: Post? = null, // 관련 게시글 (게시글 신고인 경우에만)

    @Lob
    @Column(nullable = false)
    var reason: String, // 신고 사유

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReportStatus = ReportStatus.PENDING, // 처리 상태

    val createdAt: LocalDateTime = LocalDateTime.now()
)