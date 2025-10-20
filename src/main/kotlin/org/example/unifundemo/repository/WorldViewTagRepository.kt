package org.example.unifundemo.repository

import org.example.unifundemo.domain.tag.WorldViewTag
import org.springframework.data.jpa.repository.JpaRepository

interface WorldViewTagRepository : JpaRepository<WorldViewTag, Long> {
    fun findByTag(tag: org.example.unifundemo.domain.tag.Tag): List<WorldViewTag>
    fun findByWorldViewId(worldViewId: Long): List<WorldViewTag>
}