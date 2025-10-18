package org.example.unifundemo.controller

import org.example.unifundemo.domain.post.BoardType
import org.example.unifundemo.dto.post.CreatePostRequest
import org.example.unifundemo.dto.post.PostResponse
import org.example.unifundemo.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import org.springframework.web.bind.annotation.PatchMapping

@RestController
@RequestMapping("/api/worldviews/{worldviewId}/posts") // API 주소를 세계관 하위 경로로 설정
class PostController(
    private val postService: PostService
) {
    // 자유게시판 글 작성 API
    @PostMapping("/free")
    fun createFreeBoardPost(
        @PathVariable worldviewId: Long,
        principal: Principal,
        @RequestBody request: CreatePostRequest
    ): ResponseEntity<PostResponse> {
        val post = postService.createFreeBoardPost(worldviewId, principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(post)
    }

    // 게시판 글 목록 조회 API
    @GetMapping
    fun getPosts(
        @PathVariable worldviewId: Long,
        @RequestParam boardType: BoardType
    ): ResponseEntity<List<PostResponse>> {
        val posts = postService.getPosts(worldviewId, boardType)
        return ResponseEntity.ok(posts)
    }
    @PostMapping("/works")
    fun createWorksBoardPost(
        @PathVariable worldviewId: Long,
        principal: Principal,
        @RequestBody request: CreatePostRequest
    ): ResponseEntity<PostResponse> {
        val post = postService.createWorksBoardPost(worldviewId, principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(post)
    }

    // 게시글 승인 API
    @PatchMapping("/{postId}/approve")
    fun approvePost(
        @PathVariable worldviewId: Long, // URL 구조 일관성을 위해 포함
        @PathVariable postId: Long,
        principal: Principal
    ): ResponseEntity<PostResponse> {
        val post = postService.approvePost(postId, principal.name)
        return ResponseEntity.ok(post)
    }
    @PostMapping("/{postId}/recommend")
    fun recommendPost(
        @PathVariable worldviewId: Long,
        @PathVariable postId: Long,
        principal: Principal
    ): ResponseEntity<String> {
        postService.recommendPost(postId, principal.name)
        return ResponseEntity.ok("게시글을 추천했습니다.")
    }
    @GetMapping("/{postId}")
    fun getPostDetails(
        @PathVariable worldviewId: Long,
        @PathVariable postId: Long
    ): ResponseEntity<PostResponse> {
        val post = postService.getPostDetails(postId)
        return ResponseEntity.ok(post)
    }
}