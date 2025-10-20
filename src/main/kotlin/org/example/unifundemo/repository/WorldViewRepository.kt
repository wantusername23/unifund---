package org.example.unifundemo.repository

import org.example.unifundemo.domain.worldview.WorldView
import org.springframework.data.jpa.repository.JpaRepository

interface WorldViewRepository : JpaRepository<WorldView, Long> {
    fun findByNameContainingIgnoreCaseOrKeywordsContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        name: String,
        keywords: String,
        description: String
    ): List<WorldView>

    fun findByCreatorId(creatorId: Long): List<WorldView> // 이 메소드는 findByCreatorIdOrderByIdDesc로 대체되었으므로 삭제해도 괜찮습니다.
    fun findByCreatorIdOrderByIdDesc(creatorId: Long): List<WorldView>
}