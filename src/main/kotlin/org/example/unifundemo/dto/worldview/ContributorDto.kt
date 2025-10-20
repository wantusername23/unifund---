package org.example.unifundemo.dto.worldview

import org.example.unifundemo.domain.worldview.Contributor
import org.example.unifundemo.domain.worldview.Permission

data class ContributorRequest(
    val userEmail: String,
    val permission: Permission
)

data class ContributorResponse(
    val userId: Long,
    val nickname: String,
    val permission: Permission
) {
    companion object {
        fun from(contributor: Contributor) = ContributorResponse(
            userId = contributor.user.id!!,
            nickname = contributor.user.nickname,
            permission = contributor.permission
        )
    }
}