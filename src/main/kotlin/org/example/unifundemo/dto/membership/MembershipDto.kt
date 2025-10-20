package org.example.unifundemo.dto.membership

import org.example.unifundemo.domain.membership.Membership
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero

// 멤버십 생성 요청 DTO
data class CreateMembershipRequest(
    @field:NotBlank
    val name: String,

    @field:PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    val price: Int,

    @field:NotBlank
    val description: String,

    @field:Positive(message = "레벨은 1 이상이어야 합니다.")
    val level: Int
)

// 멤버십 정보 응답 DTO
data class MembershipResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val description: String,
    val level: Int
) {
    companion object {
        fun from(membership: Membership): MembershipResponse {
            return MembershipResponse(
                id = membership.id!!,
                name = membership.name,
                price = membership.price,
                description = membership.description,
                level = membership.level
            )
        }
    }
}
data class MembershipTierRequest(
    @field:NotBlank
    val name: String,

    @field:PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    val price: Int,

    val description: String
)