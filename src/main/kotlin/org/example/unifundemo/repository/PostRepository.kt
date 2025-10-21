package org.example.unifundemo.repository

import org.example.unifundemo.domain.post.BoardType
import org.example.unifundemo.domain.post.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.example.unifundemo.domain.post.PostStatus
import org.example.unifundemo.domain.user.User
import org.example.unifundemo.domain.worldview.WorldView
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.example.unifundemo.domain.tag.Tag


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
    @Query("SELECT p FROM Post p WHERE p.recommendations >= 20 AND p.status = :status AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')))")
    fun findPopularPostsContainingQuery(@Param("query") query: String, @Param("status") status: PostStatus): List<Post>


    // ✅ 특정 세계관 내에서 태그로 승인된 게시글을 검색하는 쿼리 추가
    @Query("SELECT p FROM Post p JOIN PostTag pt ON pt.post = p WHERE p.worldview.id = :worldviewId AND pt.tag = :tag AND p.status = 'APPROVED' ORDER BY p.createdAt DESC")
    fun findByWorldviewIdAndTag(
        @Param("worldviewId") worldviewId: Long,
        @Param("tag") tag: Tag
    ): List<Post>
    @Query("SELECT p FROM Post p WHERE p.worldview.id = :worldviewId AND p.status = 'APPROVED' AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR p.content LIKE CONCAT('%', :query, '%'))")
    fun searchApprovedPostsInWorldview(
        @Param("worldviewId") worldviewId: Long,
        @Param("query") query: String
    ): List<Post>
}