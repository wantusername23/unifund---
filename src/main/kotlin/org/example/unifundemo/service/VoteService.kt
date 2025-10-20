package org.example.unifundemo.service

import jakarta.persistence.EntityNotFoundException
import org.example.unifundemo.domain.vote.UserVote
import org.example.unifundemo.domain.vote.Vote
import org.example.unifundemo.domain.vote.VoteOption
import org.example.unifundemo.domain.worldview.Permission
import org.example.unifundemo.dto.vote.CreateVoteRequest
import org.example.unifundemo.dto.vote.VoteResponse
import org.example.unifundemo.repository.*
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class VoteService(
    private val voteRepository: VoteRepository,
    private val voteOptionRepository: VoteOptionRepository,
    private val userVoteRepository: UserVoteRepository,
    private val userRepository: UserRepository,
    private val worldviewRepository: WorldViewRepository,
    private val contributorRepository: ContributorRepository,
    private val userMembershipRepository: UserMembershipRepository
) {
    fun createVote(worldviewId: Long, userEmail: String, request: CreateVoteRequest): VoteResponse {
        val user = checkEditPermission(worldviewId, userEmail)
        val worldview = worldviewRepository.getReferenceById(worldviewId)

        val vote = Vote(
            worldView = worldview,
            creator = user,
            topic = request.topic,
            endsAt = request.endsAt
        )

        request.options.forEach { optionText ->
            vote.options.add(VoteOption(vote = vote, text = optionText))
        }

        return VoteResponse.from(voteRepository.save(vote))
    }

    @Transactional(readOnly = true)
    fun getVotes(worldviewId: Long): List<VoteResponse> {
        return voteRepository.findByWorldViewId(worldviewId).map { VoteResponse.from(it) }
    }

    fun castVote(worldviewId: Long, voteId: Long, optionId: Long, userEmail: String) {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val worldview = worldviewRepository.findById(worldviewId)
            .orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        // 1. 멤버십 가입자인지 확인
        if (!userMembershipRepository.existsByUserAndMembershipWorldview(user, worldview)) {
            throw AccessDeniedException("멤버십 가입자만 투표할 수 있습니다.")
        }

        val vote = voteRepository.findById(voteId).orElseThrow { EntityNotFoundException("투표를 찾을 수 없습니다.") }

        // 2. 투표 마감 시간 확인
        if (vote.endsAt.isBefore(LocalDateTime.now())) {
            throw IllegalStateException("이미 마감된 투표입니다.")
        }

        // 3. 중복 투표 확인
        if (userVoteRepository.existsByUserIdAndVoteId(user.id!!, voteId)) {
            throw IllegalStateException("이미 투표에 참여했습니다.")
        }

        val option = voteOptionRepository.findById(optionId).orElseThrow { EntityNotFoundException("투표 항목을 찾을 수 없습니다.") }

        // 4. 투표 수 증가 및 기록
        option.voteCount++
        voteOptionRepository.save(option)
        userVoteRepository.save(UserVote(user = user, vote = vote, voteOption = option))
    }

    private fun checkEditPermission(worldviewId: Long, userEmail: String): org.example.unifundemo.domain.user.User {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val worldview = worldviewRepository.findById(worldviewId)
            .orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        val isCreator = worldview.creator.id == user.id
        val isEditor = contributorRepository.findByWorldViewIdAndUserId(worldviewId, user.id!!)
            ?.let { it.permission == Permission.EDITOR } ?: false

        if (!isCreator && !isEditor) {
            throw AccessDeniedException("투표를 생성할 권한이 없습니다.")
        }
        return user
    }
}