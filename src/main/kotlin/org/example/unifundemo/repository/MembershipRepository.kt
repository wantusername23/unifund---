package org.example.unifundemo.repository

import org.example.unifundemo.domain.membership.Membership
import org.springframework.data.jpa.repository.JpaRepository

interface MembershipRepository : JpaRepository<Membership, Long> {
    fun findByWorldviewId(worldviewId: Long): List<Membership>
}