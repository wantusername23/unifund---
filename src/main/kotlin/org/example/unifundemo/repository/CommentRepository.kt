package org.example.unifundemo.repository

import org.example.unifundemo.domain.comment.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByPostIdOrderByCreatedAtAsc(postId: Long): List<Comment>
    @Query("SELECT c FROM Comment c WHERE c.post.worldview.id = :worldviewId AND c.post.status = 'APPROVED' AND LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchCommentsInWorldview(
        @Param("worldviewId") worldviewId: Long,
        @Param("query") query: String
    ): List<Comment>
}