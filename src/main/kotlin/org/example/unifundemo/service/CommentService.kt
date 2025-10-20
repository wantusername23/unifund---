package org.example.unifundemo.service

import org.example.unifundemo.domain.comment.CommentRecommendation
import org.example.unifundemo.dto.comment.CommentResponse
import org.example.unifundemo.dto.comment.CreateCommentRequest
import org.example.unifundemo.repository.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentService(
    private val commentRepository: CommentRepository,
    private val commentRecommendationRepository: CommentRecommendationRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) {
    fun getCommentsForPost(postId: Long): List<CommentResponse> {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).map { CommentResponse.from(it) }
    }

    fun createComment(postId: Long, userEmail: String, request: CreateCommentRequest): CommentResponse {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val post = postRepository.findById(postId).orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }
        val comment = commentRepository.save(org.example.unifundemo.domain.comment.Comment(content = request.content, author = user, post = post))
        return CommentResponse.from(comment)
    }

    fun recommendComment(commentId: Long, userEmail: String) {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val comment = commentRepository.findById(commentId).orElseThrow { EntityNotFoundException("댓글을 찾을 수 없습니다.") }

        if (commentRecommendationRepository.existsByUserAndComment(user, comment)) {
            throw IllegalStateException("이미 추천한 댓글입니다.")
        }

        comment.recommendations++
        commentRepository.save(comment)
        commentRecommendationRepository.save(CommentRecommendation(user = user, comment = comment))
    }
}