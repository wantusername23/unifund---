package org.example.unifundemo.repository

import org.example.unifundemo.domain.post.BoardType
import org.example.unifundemo.domain.post.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.example.unifundemo.domain.post.PostStatus

interface PostRepository : JpaRepository<Post, Long> {
    // 특정 세계관의 특정 게시판 글 목록을 조회하는 기능
    fun findByWorldviewIdAndBoardType(worldviewId: Long, boardType: BoardType): List<Post>
    fun findByRecommendationsGreaterThanEqualAndStatusOrderByCreatedAtDesc(
        recommendations: Int,
        status: PostStatus
    ): List<Post>
}