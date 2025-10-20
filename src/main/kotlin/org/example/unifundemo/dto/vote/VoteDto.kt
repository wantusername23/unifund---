package org.example.unifundemo.dto.vote

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.example.unifundemo.domain.vote.Vote
import java.time.LocalDateTime

data class CreateVoteRequest(
    @field:NotBlank
    val topic: String,

    @field:Size(min = 2, message = "투표 항목은 최소 2개 이상이어야 합니다.")
    val options: List<String>,

    @field:Future(message = "마감 시간은 현재보다 미래여야 합니다.")
    val endsAt: LocalDateTime
)

data class VoteOptionResponse(
    val id: Long,
    val text: String,
    val voteCount: Int
)

data class VoteResponse(
    val id: Long,
    val topic: String,
    val creatorNickname: String,
    val endsAt: LocalDateTime,
    val options: List<VoteOptionResponse>,
    val totalVotes: Int
) {
    companion object {
        fun from(vote: Vote): VoteResponse {
            val options = vote.options.map {
                VoteOptionResponse(it.id!!, it.text, it.voteCount)
            }
            return VoteResponse(
                id = vote.id!!,
                topic = vote.topic,
                creatorNickname = vote.creator.nickname,
                endsAt = vote.endsAt,
                options = options,
                totalVotes = options.sumOf { it.voteCount }
            )
        }
    }
}