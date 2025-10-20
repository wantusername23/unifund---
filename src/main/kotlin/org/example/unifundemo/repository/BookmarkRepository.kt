package org.example.unifundemo.repository

import org.example.unifundemo.domain.bookmark.Bookmark
import org.example.unifundemo.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface BookmarkRepository : JpaRepository<Bookmark, Long> {
    fun existsByUserAndWorldViewId(user: User, worldViewId: Long): Boolean
    fun existsByUserAndPostId(user: User, postId: Long): Boolean
    fun findByUserOrderByCreatedAtDesc(user: User): List<Bookmark>
    fun deleteByUserAndWorldViewId(user: User, worldViewId: Long)
    fun deleteByUserAndPostId(user: User, postId: Long)
}