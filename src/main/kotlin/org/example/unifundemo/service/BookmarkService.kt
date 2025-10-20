package org.example.unifundemo.service

import jakarta.persistence.EntityNotFoundException
import org.example.unifundemo.domain.bookmark.Bookmark
import org.example.unifundemo.domain.bookmark.BookmarkType
import org.example.unifundemo.dto.bookmark.BookmarkRequest
import org.example.unifundemo.dto.bookmark.BookmarkResponse
import org.example.unifundemo.repository.BookmarkRepository
import org.example.unifundemo.repository.PostRepository
import org.example.unifundemo.repository.UserRepository
import org.example.unifundemo.repository.WorldViewRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val userRepository: UserRepository,
    private val worldViewRepository: WorldViewRepository,
    private val postRepository: PostRepository
) {
    fun addBookmark(userEmail: String, request: BookmarkRequest) {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")

        when (request.type) {
            BookmarkType.WORLDVIEW -> {
                if (bookmarkRepository.existsByUserAndWorldViewId(user, request.id)) {
                    throw IllegalStateException("이미 북마크한 세계관입니다.")
                }
                val worldView = worldViewRepository.findById(request.id).orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }
                bookmarkRepository.save(Bookmark(user = user, type = request.type, worldView = worldView))
            }
            BookmarkType.POST -> {
                if (bookmarkRepository.existsByUserAndPostId(user, request.id)) {
                    throw IllegalStateException("이미 북마크한 게시글입니다.")
                }
                val post = postRepository.findById(request.id).orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }
                bookmarkRepository.save(Bookmark(user = user, type = request.type, post = post))
            }
        }
    }

    fun removeBookmark(userEmail: String, request: BookmarkRequest) {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        when (request.type) {
            BookmarkType.WORLDVIEW -> bookmarkRepository.deleteByUserAndWorldViewId(user, request.id)
            BookmarkType.POST -> bookmarkRepository.deleteByUserAndPostId(user, request.id)
        }
    }

    @Transactional(readOnly = true)
    fun getMyBookmarks(userEmail: String): List<BookmarkResponse> {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        return bookmarkRepository.findByUserOrderByCreatedAtDesc(user).map { BookmarkResponse.from(it) }
    }
}