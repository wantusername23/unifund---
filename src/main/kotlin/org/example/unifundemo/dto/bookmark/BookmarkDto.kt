package org.example.unifundemo.dto.bookmark

import org.example.unifundemo.domain.bookmark.Bookmark
import org.example.unifundemo.domain.bookmark.BookmarkType
import java.time.LocalDateTime

data class BookmarkRequest(
    val type: BookmarkType,
    val id: Long // worldviewId 또는 postId
)

data class BookmarkResponse(
    val type: BookmarkType,
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(bookmark: Bookmark): BookmarkResponse {
            return when (bookmark.type) {
                BookmarkType.WORLDVIEW -> {
                    // ✅ Assign to a local variable first
                    val worldview = bookmark.worldView
                    if (worldview != null) {
                        BookmarkResponse(
                            type = bookmark.type,
                            // ✅ Now smart cast works on the local variable
                            id = worldview.id!!,
                            title = worldview.name,
                            createdAt = bookmark.createdAt
                        )
                    } else {
                        // Handle the unlikely case it's null
                        throw IllegalStateException("Bookmark type is WORLDVIEW but worldview is null.")
                    }
                }

                BookmarkType.POST -> {
                    // ✅ Assign to a local variable first
                    val post = bookmark.post
                    if (post != null) {
                        BookmarkResponse(
                            type = bookmark.type,
                            // ✅ Now smart cast works on the local variable
                            id = post.id!!,
                            title = post.title,
                            createdAt = bookmark.createdAt
                        )
                    } else {
                        // Handle the unlikely case it's null
                        throw IllegalStateException("Bookmark type is POST but post is null.")
                    }
                }
            }
        }
    }
}