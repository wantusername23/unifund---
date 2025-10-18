package org.example.unifundemo.domain.post

import org.example.unifundemo.domain.user.User
import org.example.unifundemo.domain.worldview.WorldView
import jakarta.persistence.*
import java.time.LocalDateTime
import jakarta.persistence.Enumerated

@Entity
class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Lob // 대용량 텍스트를 위한 어노테이션
    @Column(nullable = false)
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val boardType: BoardType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worldview_id", nullable = false)
    val worldview: WorldView,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PostStatus = PostStatus.PENDING,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),

    var recommendations: Int = 0,
    // 추천수, 인기 게시판을 위해 미리 추가
    var viewCount: Int = 0

    // 작품 게시판을 위해 상태 추가, 기본값은 PENDING
)