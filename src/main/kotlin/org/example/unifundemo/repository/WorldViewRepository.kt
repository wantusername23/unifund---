package org.example.unifundemo.repository

import org.example.unifundemo.domain.worldview.WorldView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface WorldViewRepository : JpaRepository<WorldView, Long> {
    // ✅ description 필드에 대한 검색 조건을 제거하여 오류를 해결합니다.
    @Query("SELECT w FROM WorldView w WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(w.keywords) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchWorldviews(@Param("query") query: String): List<WorldView>

    // findByCreatorId는 findByCreatorIdOrderByIdDesc로 대체되었으므로 삭제해도 괜찮습니다.
    fun findByCreatorId(creatorId: Long): List<WorldView>
    fun findByCreatorIdOrderByIdDesc(creatorId: Long): List<WorldView>
}