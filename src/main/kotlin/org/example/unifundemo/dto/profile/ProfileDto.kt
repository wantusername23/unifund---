package org.example.unifundemo.dto.profile

import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

// ✅ 프로필 수정을 위한 요청 DTO 추가
data class UpdateProfileRequest(
    @field:NotBlank
    val nickname: String,
    val bio: String?,
    val socialMediaLink: String?,
    val representativeWorldviewId: Long?
)

// 프로필 페이지에 보여줄 전체 정보
data class ProfileResponse(
    val nickname: String,
    val balance: BigDecimal,
    val showBalance: Boolean,
    val createdWorldviews: List<WorldviewInfo>,
    val participatedWorldviews: List<WorldviewInfo>,
    // ✅ 추가된 필드
    val bio: String?,
    val socialMediaLink: String?,
    val representativeWorldview: WorldviewInfo?
)

// 세계관 목록에 사용할 간단한 정보
data class WorldviewInfo(
    val id: Long,
    val name: String,
    val coverImageUrl: String
)