// src/main/kotlin/com/example/worldcreator/controller/BoardController.kt (새 파일)
package org.example.unifundemo.controller

import org.example.unifundemo.dto.post.PostResponse
import org.example.unifundemo.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/boards")
class BoardController(
    private val postService: PostService
) {
    // 인기게시판 조회 API
    @GetMapping("/popular")
    fun getPopularPosts(): ResponseEntity<List<PostResponse>> {
        val posts = postService.getPopularPosts()
        return ResponseEntity.ok(posts)
    }
}