package org.example.unifundemo.repository

import org.example.unifundemo.domain.membership.UserMembership
import org.example.unifundemo.domain.user.User
import org.example.unifundemo.domain.worldview.WorldView
import org.springframework.data.jpa.repository.JpaRepository

interface UserMembershipRepository : JpaRepository<UserMembership, Long> {
    // 유저가 특정 멤버십에 이미 가입했는지 확인하는 기능
    fun existsByUserAndMembershipId(user: User, membershipId: Long): Boolean
    fun existsByUserAndMembershipWorldview(user: User, worldview: WorldView): Boolean
}