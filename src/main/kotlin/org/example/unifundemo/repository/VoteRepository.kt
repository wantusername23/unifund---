package org.example.unifundemo.repository

import org.example.unifundemo.domain.vote.Vote
import org.springframework.data.jpa.repository.JpaRepository

interface VoteRepository : JpaRepository<Vote, Long> {
    fun findByWorldViewId(worldViewId: Long): List<Vote>
}