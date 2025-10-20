package org.example.unifundemo.repository

import org.example.unifundemo.domain.worldview.TimelineEvent
import org.springframework.data.jpa.repository.JpaRepository

interface TimelineEventRepository : JpaRepository<TimelineEvent, Long> {
    fun findByWorldViewIdOrderByCreatedAtAsc(worldViewId: Long): List<TimelineEvent>
}