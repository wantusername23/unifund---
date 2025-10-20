package org.example.unifundemo.dto.comment

import org.example.unifundemo.domain.comment.Comment
import java.time.LocalDateTime

data class CreateCommentRequest(
    val content: String
)

data class CommentResponse(
    val id: Long,
    val content: String,
    val authorNickname: String,
    val createdAt: LocalDateTime,
    val recommendations: Int
) {
    companion object {
        fun from(comment: Comment) = CommentResponse(
            id = comment.id!!,
            content = comment.content,
            authorNickname = comment.author.nickname,
            createdAt = comment.createdAt,
            recommendations = comment.recommendations
        )
    }
}