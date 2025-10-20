package org.example.unifundemo.domain.tag

import jakarta.persistence.*
import org.example.unifundemo.domain.post.Post

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["post_id", "tag_id"])])
class PostTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    val tag: Tag
)