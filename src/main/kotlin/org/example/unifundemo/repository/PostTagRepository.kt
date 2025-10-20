package org.example.unifundemo.repository

import org.example.unifundemo.domain.tag.PostTag
import org.springframework.data.jpa.repository.JpaRepository

interface PostTagRepository : JpaRepository<PostTag, Long> {
    fun findByPostId(postId: Long): List<PostTag>
    fun findByTag(tag: org.example.unifundemo.domain.tag.Tag): List<PostTag>
}