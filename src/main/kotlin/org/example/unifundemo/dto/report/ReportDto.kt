package org.example.unifundemo.dto.report

import jakarta.validation.constraints.NotBlank

data class CreateReportRequest(
    @field:NotBlank
    val reason: String,
    val worldviewId: Long,
    val postId: Long? = null
)