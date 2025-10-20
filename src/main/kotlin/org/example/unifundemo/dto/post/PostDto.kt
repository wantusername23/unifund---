package org.example.unifundemo.dto.post

// 💡 FIX: Make sure these imports are correct
import org.example.unifundemo.domain.post.Post
import java.time.LocalDateTime


data class CreatePostRequest(
    val title: String,
    val content: String,
    val isNotice: Boolean? = false,
    val tags: Set<String> = emptySet()
)

data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorNickname: String,
    val createdAt: LocalDateTime,
    val recommendations: Int,
    val isNotice: Boolean,
    val tags: Set<String>
) {
    companion object {
        fun from(post: Post, tags: Set<String>): PostResponse {
            return PostResponse(
                id = post.id!!, // 💡 FIX: Added "!!" to handle the nullable id
                title = post.title,
                content = post.content,
                authorNickname = post.author.nickname,
                createdAt = post.createdAt,
                recommendations = post.recommendations,
                isNotice = post.isNotice,
                tags = tags
            )
        }
    }
}