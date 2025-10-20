package org.example.unifundemo.repository

import org.example.unifundemo.domain.post.BoardType
import org.example.unifundemo.domain.post.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.example.unifundemo.domain.post.PostStatus
import org.example.unifundemo.domain.user.User
import org.example.unifundemo.domain.worldview.WorldView
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PostRepository : JpaRepository<Post, Long> {
    // 특정 세계관의 특정 게시판 글 목록을 조회하는 기능
    fun findByWorldviewIdAndBoardTypeOrderByIsNoticeDescCreatedAtDesc(worldviewId: Long, boardType: BoardType): List<Post>
    fun findByRecommendationsGreaterThanEqualAndStatusOrderByCreatedAtDesc(
        recommendations: Int,
        status: PostStatus
    ): List<Post>
    fun findByWorldviewIdAndStatus(worldviewId: Long, status: PostStatus): List<Post>
    @Query("SELECT DISTINCT p.worldview FROM Post p WHERE p.author = :author")
    fun findDistinctWorldviewByAuthor(author: User): List<WorldView>
    @Query("SELECT p FROM Post p WHERE p.recommendations >= 20 AND p.status = 'APPROVED' AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    fun findPopularPostsContainingQuery(@Param("query") query: String): List<Post>
}