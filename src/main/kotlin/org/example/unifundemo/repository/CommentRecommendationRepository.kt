package org.example.unifundemo.repository

import org.example.unifundemo.domain.comment.Comment
import org.example.unifundemo.domain.comment.CommentRecommendation
import org.example.unifundemo.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRecommendationRepository : JpaRepository<CommentRecommendation, Long> {
    fun existsByUserAndComment(user: User, comment: Comment): Boolean
}