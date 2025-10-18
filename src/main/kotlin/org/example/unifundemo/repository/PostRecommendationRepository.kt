package org.example.unifundemo.repository

import org.example.unifundemo.domain.post.Post
import org.example.unifundemo.domain.post.PostRecommendation
import org.example.unifundemo.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface PostRecommendationRepository : JpaRepository<PostRecommendation, Long> {
    fun existsByUserAndPost(user: User, post: Post): Boolean
}