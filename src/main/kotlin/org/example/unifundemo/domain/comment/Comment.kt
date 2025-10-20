package org.example.unifundemo.domain.comment

import org.example.unifundemo.domain.post.Post
import org.example.unifundemo.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Comment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 1000)
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    val post: Post,

    var recommendations: Int = 0,

    val createdAt: LocalDateTime = LocalDateTime.now()
)