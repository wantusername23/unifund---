package org.example.unifundemo.repository

import org.example.unifundemo.domain.accounting.DistributionHistory
import org.springframework.data.jpa.repository.JpaRepository

interface DistributionHistoryRepository : JpaRepository<DistributionHistory, Long> {
    fun findByWorldviewIdOrderByIdDesc(worldviewId: Long): List<DistributionHistory>
}