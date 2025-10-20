package org.example.unifundemo.repository

import org.example.unifundemo.domain.worldview.Contributor
import org.springframework.data.jpa.repository.JpaRepository

interface ContributorRepository : JpaRepository<Contributor, Long> {
    fun findByWorldViewId(worldViewId: Long): List<Contributor>
    fun findByWorldViewIdAndUserId(worldViewId: Long, userId: Long): Contributor?
}