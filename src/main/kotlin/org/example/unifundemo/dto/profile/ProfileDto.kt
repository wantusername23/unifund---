package org.example.unifundemo.dto.profile

import java.math.BigDecimal

// 프로필 페이지에 보여줄 전체 정보
data class ProfileResponse(
    val nickname: String,
    val balance: BigDecimal,
    val showBalance: Boolean,
    val createdWorldviews: List<WorldviewInfo>,
    val participatedWorldviews: List<WorldviewInfo>
)

// 세계관 목록에 사용할 간단한 정보
data class WorldviewInfo(
    val id: Long,
    val name: String,
    val coverImageUrl: String
)