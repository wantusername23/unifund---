package org.example.unifundemo.repository

import org.example.unifundemo.domain.worldview.WorldView
import org.springframework.data.jpa.repository.JpaRepository

interface WorldViewRepository : JpaRepository<WorldView, Long> {
    fun findByNameContainingIgnoreCaseOrKeywordsContainingIgnoreCase(name: String, keywords: String): List<WorldView>
    fun findByCreatorId(creatorId: Long): List<WorldView>
}