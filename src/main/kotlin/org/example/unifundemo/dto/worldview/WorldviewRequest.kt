package org.example.unifundemo.dto.worldview

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.example.unifundemo.dto.membership.MembershipTierRequest

data class CreateWorldviewRequest(
    @field:NotBlank(message = "세계관 이름은 비워둘 수 없습니다.")
    @field:Size(max = 100, message = "세계관 이름은 100자를 넘을 수 없습니다.")
    val name: String,

    @field:NotBlank(message = "세계관 설명은 비워둘 수 없습니다.")
    val description: String,

    val keywords: String,

    val coverImageUrl: String?,

    @field:Valid
    val lowTier: MembershipTierRequest,

    @field:Valid
    val highTier: MembershipTierRequest,

    val tags: Set<String> = emptySet()
)