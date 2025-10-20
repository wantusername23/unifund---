package org.example.unifundemo.controller

import org.example.unifundemo.dto.bookmark.BookmarkRequest
import org.example.unifundemo.dto.bookmark.BookmarkResponse
import org.example.unifundemo.service.BookmarkService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/bookmarks")
class BookmarkController(
    private val bookmarkService: BookmarkService
) {
    @PostMapping
    fun addBookmark(principal: Principal, @RequestBody request: BookmarkRequest): ResponseEntity<String> {
        bookmarkService.addBookmark(principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body("북마크에 추가되었습니다.")
    }

    @DeleteMapping
    fun removeBookmark(principal: Principal, @RequestBody request: BookmarkRequest): ResponseEntity<String> {
        bookmarkService.removeBookmark(principal.name, request)
        return ResponseEntity.ok("북마크에서 삭제되었습니다.")
    }

    @GetMapping
    fun getMyBookmarks(principal: Principal): ResponseEntity<List<BookmarkResponse>> {
        val bookmarks = bookmarkService.getMyBookmarks(principal.name)
        return ResponseEntity.ok(bookmarks)
    }
}