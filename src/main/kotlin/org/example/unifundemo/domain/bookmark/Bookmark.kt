package org.example.unifundemo.domain.bookmark

import jakarta.persistence.*
import org.example.unifundemo.domain.post.Post
import org.example.unifundemo.domain.user.User
import org.example.unifundemo.domain.worldview.WorldView
import java.time.LocalDateTime

@Entity
@Table(uniqueConstraints = [
    UniqueConstraint(columnNames = ["user_id", "world_view_id"]),
    UniqueConstraint(columnNames = ["user_id", "post_id"])
])
class Bookmark(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: BookmarkType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_view_id")
    val worldView: WorldView? = null, // 세계관 북마크인 경우

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: Post? = null, // 게시글 북마크인 경우

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)