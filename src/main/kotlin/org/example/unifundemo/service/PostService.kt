package org.example.unifundemo.service

import org.example.unifundemo.domain.post.BoardType
import org.example.unifundemo.domain.post.Post
import org.example.unifundemo.dto.post.CreatePostRequest
import org.example.unifundemo.dto.post.PostResponse
import org.example.unifundemo.repository.*
import org.example.unifundemo.domain.post.PostRecommendation
import org.example.unifundemo.repository.PostRecommendationRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.example.unifundemo.domain.post.PostStatus
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val worldviewRepository: WorldViewRepository,
    private val userMembershipRepository: UserMembershipRepository,
    private val postRecommendationRepository: PostRecommendationRepository
) {
    // 자유게시판 글 작성
    fun createFreeBoardPost(worldviewId: Long, userEmail: String, request: CreatePostRequest): PostResponse {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val worldview = worldviewRepository.findById(worldviewId).orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        // 🛡️ 권한 검사: 레벨 1 이상 멤버십 가입자인지 확인
        val subscription = userMembershipRepository.findByUserAndMembershipWorldview(user, worldview)
            ?: throw AccessDeniedException("멤버십 가입자만 글을 작성할 수 있습니다.")

        if (subscription.membership.level < 1) { // 사실상 모든 멤버십 허용
            throw AccessDeniedException("이 게시판에 글을 작성할 권한이 없습니다.")
        }

        val post = Post(
            title = request.title,
            content = request.content,
            boardType = BoardType.FREE,
            author = user,
            worldview = worldview,
            status = PostStatus.APPROVED
        )

        val savedPost = postRepository.save(post)
        return PostResponse.from(savedPost)
    }
    fun createWorksBoardPost(worldviewId: Long, userEmail: String, request: CreatePostRequest): PostResponse {
        // ... 권한 검사 로직은 동일 ...
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val worldview = worldviewRepository.findById(worldviewId).orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        // 🛡️ 권한 검사: 레벨 2 이상 멤버십 가입자인지 확인
        val subscription = userMembershipRepository.findByUserAndMembershipWorldview(user, worldview)
            ?: throw AccessDeniedException("작품 게시판은 특정 등급 이상의 멤버십 가입자만 작성할 수 있습니다.")

        if (subscription.membership.level < 2) {
            throw AccessDeniedException("작품 게시판에 글을 작성할 수 있는 멤버십 등급이 아닙니다.")
        }
        if (request.isNotice == true && worldview.creator.email != userEmail) {
            throw AccessDeniedException("공지는 세계관 창작자만 작성할 수 있습니다.")
        }

        val post = Post(
            title = request.title,
            content = request.content,
            boardType = BoardType.WORKS,
            author = user,
            worldview = worldview
            // status는 기본값인 PENDING으로 자동 설정됨
        )
        val savedPost = postRepository.save(post)
        return PostResponse.from(savedPost)
    }

    // 창작자의 게시글 승인 (새 메소드)
    fun approvePost(postId: Long, userEmail: String): PostResponse {
        val post = postRepository.findById(postId).orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }

        // 🛡️ 권한 검사: 요청자가 이 글이 속한 세계관의 창작자인지 확인
        if (post.worldview.creator.email != userEmail) {
            throw AccessDeniedException("이 게시글을 승인할 권한이 없습니다.")
        }

        post.status = PostStatus.APPROVED
        post.updatedAt = LocalDateTime.now()
        val savedPost = postRepository.save(post)
        return PostResponse.from(savedPost)
    }

    // 게시판 글 목록 조회
    @Transactional(readOnly = true)
    fun getPosts(worldviewId: Long, boardType: BoardType): List<PostResponse> {
        // ✅ 수정된 메소드 호출
        return postRepository.findByWorldviewIdAndBoardTypeOrderByIsNoticeDescCreatedAtDesc(worldviewId, boardType)
            .map { post -> PostResponse.from(post) }
    }
    fun recommendPost(postId: Long, userEmail: String) {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val post = postRepository.findById(postId).orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }

        // 추천은 승인된 글에만 가능
        if (post.status != PostStatus.APPROVED) {
            throw IllegalStateException("승인된 게시글만 추천할 수 있습니다.")
        }

        // 🛡️ 중복 추천 방지
        if (postRecommendationRepository.existsByUserAndPost(user, post)) {
            throw IllegalStateException("이미 추천한 게시글입니다.")
        }

        // 추천 기록 생성
        val recommendation = PostRecommendation(user = user, post = post)
        postRecommendationRepository.save(recommendation)

        // 게시글의 추천수 업데이트
        post.recommendations++
        postRepository.save(post)
    }
    @Transactional(readOnly = true)
    fun getPopularPosts(): List<PostResponse> {
        val popularPosts = postRepository.findByRecommendationsGreaterThanEqualAndStatusOrderByCreatedAtDesc(
            recommendations = 20,
            status = PostStatus.APPROVED
        )
        return popularPosts.map { PostResponse.from(it) }
    }
    fun getPostDetails(postId: Long): PostResponse {
        val post = postRepository.findById(postId).orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }

        // 💡 조회수 증가 로직
        post.viewCount++

        // 💡 조회수 1당 10원의 가상 수익이 발생하여 세계관의 수익 풀에 누적된다고 가정
        val revenuePerView = BigDecimal("3")
        post.worldview.revenuePool = post.worldview.revenuePool.add(revenuePerView)

        return PostResponse.from(postRepository.save(post))
    }
    @Transactional(readOnly = true)
    fun getPendingPosts(worldviewId: Long, userEmail: String): List<PostResponse> {
        val worldview = worldviewRepository.findById(worldviewId)
            .orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        // 🛡️ 권한 검사: 요청자가 세계관의 창작자인지 확인
        if (worldview.creator.email != userEmail) {
            throw AccessDeniedException("승인 대기 목록을 조회할 권한이 없습니다.")
        }

        return postRepository.findByWorldviewIdAndStatus(worldviewId, PostStatus.PENDING)
            .map { post -> PostResponse.from(post) }
    }
}