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
import org.example.unifundemo.domain.worldview.Permission
import org.example.unifundemo.repository.ContributorRepository
import org.example.unifundemo.domain.tag.PostTag
import org.example.unifundemo.repository.PostTagRepository
import org.example.unifundemo.repository.TagRepository


@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val worldviewRepository: WorldViewRepository,
    private val userMembershipRepository: UserMembershipRepository,
    private val postRecommendationRepository: PostRecommendationRepository,
    private val contributorRepository: ContributorRepository,
    private val tagService: TagService,
    private val tagRepository: TagRepository,
    private val postTagRepository: PostTagRepository,
    private val notificationService: NotificationService
) {
    // ✅ 게시글 태그 처리 로직 (내부 헬퍼)
    private fun processPostTags(post: Post, tagNames: Set<String>) {
        if (tagNames.isEmpty()) return

        val tags = tagService.findOrCreateTags(tagNames)
        val postTags = tags.map { tag ->
            PostTag(post = post, tag = tag)
        }
        postTagRepository.saveAll(postTags)
    }

    // ✅ 게시글 ID로 태그 이름 Set을 조회하는 로직 (내부 헬퍼)

    private fun getTagsForPost(postId: Long): Set<String> {
        return postTagRepository.findByPostId(postId)
            .map { it.tag.name }
            .toSet()
    }
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
        processPostTags(savedPost, request.tags)
        return PostResponse.from(savedPost, request.tags)
    }
    fun createWorksBoardPost(worldviewId: Long, userEmail: String, request: CreatePostRequest): PostResponse {
        // ... 권한 검사 로직은 동일 ...
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val worldview = worldviewRepository.findById(worldviewId).orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        // 🛡️ 권한 검사: 레벨 2 이상 멤버십 가입자인지 확인
        val isContributorWithEditPermission = contributorRepository.findByWorldViewIdAndUserId(worldview.id!!, user.id!!)
            ?.let { it.permission == Permission.EDITOR } ?: false

        val subscription = userMembershipRepository.findByUserAndMembershipWorldview(user, worldview)

        if (subscription == null && !isContributorWithEditPermission) {
            throw AccessDeniedException("작품 게시판은 특정 등급 이상의 멤버십 가입자 또는 편집 권한이 있는 공동 창작자만 작성할 수 있습니다.")
        }

        if (subscription != null && subscription.membership.level < 2 && !isContributorWithEditPermission) {
            throw AccessDeniedException("작품 게시판에 글을 작성할 수 있는 멤버십 등급이 아닙니다.")
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
        processPostTags(savedPost, request.tags)
        return PostResponse.from(savedPost, request.tags)
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
        val tags = getTagsForPost(savedPost.id!!)
        return PostResponse.from(savedPost, tags)
    }

    // 게시판 글 목록 조회
    @Transactional(readOnly = true)
    fun getPosts(worldviewId: Long, boardType: BoardType): List<PostResponse> {
        // ✅ 수정된 메소드 호출
        return postRepository.findByWorldviewIdAndBoardTypeOrderByIsNoticeDescCreatedAtDesc(worldviewId, boardType)
            .map { post -> val tags = getTagsForPost(post.id!!)
                PostResponse.from(post, tags) }
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

        if (post.author.id != user.id) {
            val message = "${user.nickname}님이 '${post.title}' 게시글을 추천했습니다."
            notificationService.sendNotification(post.author, message)
        }
    }
    @Transactional(readOnly = true)
    fun getPopularPosts(): List<PostResponse> {
        val popularPosts = postRepository.findByRecommendationsGreaterThanEqualAndStatusOrderByCreatedAtDesc(
            recommendations = 20,
            status = PostStatus.APPROVED
        )
        return popularPosts.map { post ->
            // ✅ 태그 조회 로직 추가
            val tags = getTagsForPost(post.id!!)
            PostResponse.from(post, tags) // ✅ DTO에 tags 전달
        }
    }
    fun getPostDetails(postId: Long): PostResponse {
        val post = postRepository.findById(postId).orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }

        // 💡 조회수 증가 로직
        post.viewCount++

        // 💡 조회수 1당 10원의 가상 수익이 발생하여 세계관의 수익 풀에 누적된다고 가정
        val revenuePerView = BigDecimal("3")
        post.worldview.revenuePool = post.worldview.revenuePool.add(revenuePerView)

        val tags = getTagsForPost(post.id!!)
        return PostResponse.from(postRepository.save(post), tags)
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
            .map { post -> val tags = getTagsForPost(post.id!!)
                PostResponse.from(post, tags) }
    }
    @Transactional(readOnly = true)
    fun findPostsByTag(worldviewId: Long, tagName: String): List<PostResponse> { // ✅ worldviewId 파라미터 추가
        val tag = tagRepository.findByName(tagName) ?: return emptyList()

        // ✅ 새로 만든 쿼리 메서드를 호출하여 worldviewId와 tag로 동시에 검색
        val posts = postRepository.findByWorldviewIdAndTag(worldviewId, tag)

        return posts.map { post ->
            val tags = getTagsForPost(post.id!!)
            PostResponse.from(post, tags)
        }
    }
}