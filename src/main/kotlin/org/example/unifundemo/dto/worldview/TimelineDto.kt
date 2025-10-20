package org.example.unifundemo.dto.worldview

import jakarta.validation.constraints.NotBlank
import org.example.unifundemo.domain.worldview.TimelineEvent
import java.time.LocalDateTime

data class TimelineEventRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val description: String,
    @field:NotBlank val eventDate: String
)

data class TimelineEventResponse(
    val id: Long,
    val title: String,
    val description: String,
    val eventDate: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(event: TimelineEvent) = TimelineEventResponse(
            id = event.id!!,
            title = event.title,
            description = event.description,
            eventDate = event.eventDate,
            createdAt = event.createdAt
        )
    }
}