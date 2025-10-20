package org.example.unifundemo.controller

import org.example.unifundemo.dto.comment.CommentResponse
import org.example.unifundemo.dto.comment.CreateCommentRequest
import org.example.unifundemo.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/posts/{postId}/comments")
class CommentController(
    private val commentService: CommentService
) {
    @GetMapping
    fun getComments(@PathVariable postId: Long): ResponseEntity<List<CommentResponse>> {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId))
    }

    @PostMapping
    fun createComment(
        @PathVariable postId: Long,
        principal: Principal,
        @RequestBody request: CreateCommentRequest
    ): ResponseEntity<CommentResponse> {
        val comment = commentService.createComment(postId, principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(comment)
    }

    @PostMapping("/{commentId}/recommend")
    fun recommendComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        principal: Principal
    ): ResponseEntity<Unit> {
        commentService.recommendComment(commentId, principal.name)
        return ResponseEntity.ok().build()
    }
}