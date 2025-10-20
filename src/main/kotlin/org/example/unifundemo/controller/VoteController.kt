package org.example.unifundemo.controller

import org.example.unifundemo.dto.vote.CreateVoteRequest
import org.example.unifundemo.dto.vote.VoteResponse
import org.example.unifundemo.service.VoteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/worldviews/{worldviewId}/votes")
class VoteController(
    private val voteService: VoteService
) {
    @PostMapping
    fun createVote(
        @PathVariable worldviewId: Long,
        principal: Principal,
        @RequestBody request: CreateVoteRequest
    ): ResponseEntity<VoteResponse> {
        val vote = voteService.createVote(worldviewId, principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(vote)
    }

    @GetMapping
    fun getVotes(@PathVariable worldviewId: Long): ResponseEntity<List<VoteResponse>> {
        val votes = voteService.getVotes(worldviewId)
        return ResponseEntity.ok(votes)
    }

    @PostMapping("/{voteId}/options/{optionId}")
    fun castVote(
        @PathVariable worldviewId: Long,
        @PathVariable voteId: Long,
        @PathVariable optionId: Long,
        principal: Principal
    ): ResponseEntity<String> {
        voteService.castVote(worldviewId, voteId, optionId, principal.name)
        return ResponseEntity.ok("투표가 완료되었습니다.")
    }
}