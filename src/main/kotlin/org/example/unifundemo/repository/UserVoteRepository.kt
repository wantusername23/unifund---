package org.example.unifundemo.repository

import org.example.unifundemo.domain.vote.UserVote
import org.springframework.data.jpa.repository.JpaRepository

interface UserVoteRepository : JpaRepository<UserVote, Long> {
    fun existsByUserIdAndVoteId(userId: Long, voteId: Long): Boolean
}