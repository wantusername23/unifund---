package org.example.unifundemo.repository

import org.example.unifundemo.domain.vote.VoteOption
import org.springframework.data.jpa.repository.JpaRepository

interface VoteOptionRepository : JpaRepository<VoteOption, Long>