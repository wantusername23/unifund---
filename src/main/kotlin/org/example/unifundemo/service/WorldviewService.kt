package org.example.unifundemo.service

import org.example.unifundemo.domain.worldview.WorldView
import org.example.unifundemo.dto.worldview.CreateWorldviewRequest
import org.example.unifundemo.repository.UserRepository
import org.example.unifundemo.repository.WorldViewRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.example.unifundemo.dto.worldview.WorldviewDetailResponse
import org.example.unifundemo.dto.worldview.WorldviewSimpleResponse
import jakarta.persistence.EntityNotFoundException
import org.example.unifundemo.domain.membership.Membership
import org.example.unifundemo.dto.membership.CreateMembershipRequest
import org.example.unifundemo.dto.membership.MembershipResponse
import org.example.unifundemo.repository.MembershipRepository
import org.springframework.security.access.AccessDeniedException
import org.example.unifundemo.domain.membership.UserMembership
import org.example.unifundemo.repository.UserMembershipRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID
import org.example.unifundemo.domain.accounting.DistributionHistory
import org.example.unifundemo.domain.accounting.RevenueShare
import org.example.unifundemo.repository.DistributionHistoryRepository
import org.example.unifundemo.repository.RevenueShareRepository
import java.math.BigDecimal
import java.math.RoundingMode
import org.example.unifundemo.repository.PostRepository
import org.example.unifundemo.domain.post.PostStatus
import org.example.unifundemo.domain.worldview.Contributor
import org.example.unifundemo.domain.worldview.Permission
import org.example.unifundemo.dto.worldview.ContributorRequest
import org.example.unifundemo.dto.worldview.ContributorResponse
import org.example.unifundemo.repository.ContributorRepository
import org.example.unifundemo.domain.tag.WorldViewTag
import org.example.unifundemo.repository.TagRepository
import org.example.unifundemo.repository.WorldViewTagRepository
import org.example.unifundemo.service.NotificationService


@Service
@Transactional
class WorldviewService(
    private val worldviewRepository: WorldViewRepository,
    private val userRepository: UserRepository,
    private val membershipRepository: MembershipRepository,
    private val userMembershipRepository: UserMembershipRepository,
    @Value("\${file.upload-dir}") private val uploadDir: String,
    private val distributionHistoryRepository: DistributionHistoryRepository, // ✅ 추가
    private val revenueShareRepository: RevenueShareRepository,
    private val postRepository: PostRepository,
    private val contributorRepository: ContributorRepository,
    private val tagService: TagService,
    private val tagRepository: TagRepository,
    private val worldViewTagRepository: WorldViewTagRepository,
    private val notificationService: NotificationService
) {
    fun createWorldview(email: String, request: CreateWorldviewRequest, file: MultipartFile): WorldView {
        // ✅ createAndSaveWorldview가 worldview를 반환하므로 tags를 request에서 가져옴
        val worldview = createAndSaveWorldview(email, request, file)
        processWorldViewTags(worldview, request.tags) // ✅ 태그 처리 로직 호출
        createMembershipTiers(worldview, request)
        return worldview
    }

    // AI 이미지 URL 방식
    fun createWorldview(email: String, request: CreateWorldviewRequest): WorldView {
        val worldview = createAndSaveWorldview(email, request)
        processWorldViewTags(worldview, request.tags) // ✅ 태그 처리 로직 호출
        createMembershipTiers(worldview, request)
        return worldview
    }

    // 중복 로직을 분리한 내부 메소드들
    private fun createAndSaveWorldview(email: String, request: CreateWorldviewRequest, file: MultipartFile? = null): WorldView {
        val creator = userRepository.findByEmail(email) ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

        val imageUrl = if (file != null) {
            val filename = "${java.util.UUID.randomUUID()}-${file.originalFilename}"
            val filePath = java.nio.file.Paths.get(uploadDir, filename)
            java.nio.file.Files.createDirectories(filePath.parent)
            java.nio.file.Files.copy(file.inputStream, filePath)
            "/images/$filename"
        } else {
            require(!request.coverImageUrl.isNullOrBlank()) { "이미지 파일 또는 URL이 필요합니다." }
            request.coverImageUrl
        }

        val worldview = WorldView(
            name = request.name,
            description = request.description,
            keywords = request.keywords,
            coverImageUrl = imageUrl,
            creator = creator
        )
        return worldviewRepository.save(worldview)
    }
    private fun processWorldViewTags(worldView: WorldView, tagNames: Set<String>) {
        if (tagNames.isEmpty()) return

        val tags = tagService.findOrCreateTags(tagNames)
        val worldViewTags = tags.map { tag ->
            WorldViewTag(worldView = worldView, tag = tag)
        }
        worldViewTagRepository.saveAll(worldViewTags)
    }
    // ✅ 세계관 ID로 태그 이름 Set을 조회하는 로직 (내부 헬퍼)

    private fun getTagsForWorldview(worldviewId: Long): Set<String> {
        return worldViewTagRepository.findByWorldViewId(worldviewId)
            .map { it.tag.name }
            .toSet()
    }

    private fun createMembershipTiers(worldview: WorldView, request: CreateWorldviewRequest) {
        val lowTierMembership = Membership(
            name = request.lowTier.name,
            price = request.lowTier.price,
            description = request.lowTier.description,
            level = 1, // 낮은 단계는 레벨 1
            worldview = worldview
        )
        val highTierMembership = Membership(
            name = request.highTier.name,
            price = request.highTier.price,
            description = request.highTier.description,
            level = 2, // 높은 단계는 레벨 2
            worldview = worldview
        )
        membershipRepository.saveAll(listOf(lowTierMembership, highTierMembership))
    }
    // 전체 세계관 목록 조회 메소드 추가
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션은 성능에 이점이 있음
    fun getAllWorldviews(userEmail: String?): List<WorldviewSimpleResponse> {
        return worldviewRepository.findAll()
            .map { worldview ->
                val isCreator = userEmail?.let { it == worldview.creator.email } ?: false
                // ✅ 태그 조회 로직 추가
                val tags = getTagsForWorldview(worldview.id!!)
                WorldviewSimpleResponse.from(worldview, isCreator, tags) // ✅ DTO에 tags 전달
            }
    }

    // ✅ 메소드 시그니처 변경 (userEmail: String?)
    @Transactional(readOnly = true)
    fun getWorldviewById(id: Long, userEmail: String?): WorldviewDetailResponse {
        val worldview = worldviewRepository.findById(id)
            .orElseThrow { EntityNotFoundException("해당 ID의 세계관을 찾을 수 없습니다: $id") }
        val isCreator = userEmail?.let { it == worldview.creator.email } ?: false
        // ✅ 태그 조회 로직 추가
        val tags = getTagsForWorldview(worldview.id!!)
        return WorldviewDetailResponse.from(worldview, isCreator, tags) // ✅ DTO에 tags 전달
    }
    // 멤버십 등급 추가 메소드
    fun addMembershipTier(worldviewId: Long, userEmail: String, request: CreateMembershipRequest): MembershipResponse {
        val worldview = worldviewRepository.findById(worldviewId)
            .orElseThrow { EntityNotFoundException("해당 ID의 세계관을 찾을 수 없습니다: $worldviewId") }

        // 🛡️ 권한 검사: 요청한 사용자가 세계관의 창작자인지 확인합니다.
        if (worldview.creator.email != userEmail) {
            throw AccessDeniedException("멤버십을 설정할 권한이 없습니다.")
        }

        val membership = Membership(
            name = request.name,
            price = request.price,
            description = request.description,
            level = request.level,
            worldview = worldview
        )

        val savedMembership = membershipRepository.save(membership)
        return MembershipResponse.from(savedMembership)
    }
    @Transactional(readOnly = true)
    fun getMembershipTiers(worldviewId: Long): List<MembershipResponse> {
        // 먼저 세계관이 존재하는지 확인합니다.
        if (!worldviewRepository.existsById(worldviewId)) {
            throw EntityNotFoundException("해당 ID의 세계관을 찾을 수 없습니다: $worldviewId")
        }

        return membershipRepository.findByWorldviewId(worldviewId)
            .map { membership -> MembershipResponse.from(membership) }
    }
    fun subscribeToMembership(membershipId: Long, userEmail: String) {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")

        val membership = membershipRepository.findById(membershipId)
            .orElseThrow { EntityNotFoundException("해당 ID의 멤버십을 찾을 수 없습니다: $membershipId") }

        // ... (기존 가입 로직)
        val userMembership = UserMembership(
            user = user,
            membership = membership
        )
        userMembershipRepository.save(userMembership)

        // ✅ 알림 발송 로직 추가
        val creator = membership.worldview.creator
        // 본인이 본인 세계관에 가입하는 경우는 제외
        if (creator.id != user.id) {
            val message = "${user.nickname}님이 '${membership.worldview.name}'의 '${membership.name}' 멤버십에 가입했습니다."
            notificationService.sendNotification(creator, message)
        }
    }
    @Transactional(readOnly = true)
    fun searchWorldviews(query: String, userEmail: String?): List<WorldviewSimpleResponse> {
        // 1. 세계관의 제목, 키워드, 설명에서 직접 검색
        // ✅ 수정된 Repository 메서드 호출
        val directResults = worldviewRepository.searchWorldviews(query)

        // 2. 인기 게시글의 제목, 내용에서 검색하여 관련 세계관 찾기
        val popularPosts = postRepository.findPopularPostsContainingQuery(query, PostStatus.APPROVED)
        val postRelatedResults = popularPosts.map { it.worldview }

        // 3. 두 검색 결과를 합치고 중복을 제거
        val combinedResults = (directResults + postRelatedResults).distinctBy { it.id }

        // 4. 결과를 DTO로 변환하여 반환
        return combinedResults.map { worldview ->
            val isCreator = userEmail?.let { it == worldview.creator.email } ?: false
            val tags = getTagsForWorldview(worldview.id!!)
            WorldviewSimpleResponse.from(worldview, isCreator, tags)
        }
    }
    // ✅ 태그 기반 세계관 검색 메서드 추가
    @Transactional(readOnly = true)
    fun findWorldviewsByTag(tagName: String, userEmail: String?): List<WorldviewSimpleResponse> {
        val tag = tagRepository.findByName(tagName) ?: return emptyList()
        val worldviews = worldViewTagRepository.findByTag(tag).map { it.worldView }

        return worldviews.map { worldview ->
            val isCreator = userEmail?.let { it == worldview.creator.email } ?: false
            val tags = getTagsForWorldview(worldview.id!!)
            WorldviewSimpleResponse.from(worldview, isCreator, tags)
        }
    }

    @Transactional(readOnly = true)
    fun getWorldviewForAdmin(worldviewId: Long, userEmail: String): Map<String, Any> {
        val worldview = worldviewRepository.findById(worldviewId)
            .orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        // 🛡️ 권한 검사
        if (worldview.creator.email != userEmail) {
            throw AccessDeniedException("세계관 관리 정보에 접근할 권한이 없습니다.")
        }

        return mapOf(
            "id" to worldview.id!!,
            "name" to worldview.name,
            "revenuePool" to worldview.revenuePool
        )
    }

    // ✅ distributeRevenue 메소드를 아래 코드로 전체 교체
    fun distributeRevenue(worldviewId: Long, userEmail: String) {
        val worldview = worldviewRepository.findById(worldviewId)
            .orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        if (worldview.creator.email != userEmail) {
            throw AccessDeniedException("수익을 분배할 권한이 없습니다.")
        }

        val totalRevenue = worldview.revenuePool
        if (totalRevenue <= BigDecimal.ZERO) {
            throw IllegalStateException("분배할 수익이 없습니다.")
        }

        // 1. 분배 기록 생성
        val history = distributionHistoryRepository.save(DistributionHistory(worldview = worldview, totalAmount = totalRevenue))

        // 2. 각 그룹별 수익 계산
        val creatorShare = totalRevenue.multiply(BigDecimal("0.3"))
        val popularAuthorsShare = totalRevenue.multiply(BigDecimal("0.4"))
        val membersShare = totalRevenue.multiply(BigDecimal("0.3"))

        // 3. 창작자에게 수익 분배
        val creator = worldview.creator
        creator.balance = creator.balance.add(creatorShare)
        userRepository.save(creator)
        revenueShareRepository.save(RevenueShare(history = history, user = creator, amount = creatorShare, description = "창작자 수익"))

        // 4. 인기 글 작성자들에게 분배
        val popularPosts = postRepository.findByRecommendationsGreaterThanEqualAndStatusOrderByCreatedAtDesc(20, PostStatus.APPROVED)
            .filter { it.worldview.id == worldviewId }

        if (popularPosts.isNotEmpty()) {
            val totalRecommendations = popularPosts.sumOf { it.recommendations }.toBigDecimal()
            popularPosts.forEach { post ->
                val author = post.author
                val contribution = post.recommendations.toBigDecimal().divide(totalRecommendations, 10, RoundingMode.HALF_UP)
                val authorReward = popularAuthorsShare.multiply(contribution)

                author.balance = author.balance.add(authorReward)
                userRepository.save(author)
                revenueShareRepository.save(RevenueShare(history = history, user = author, amount = authorReward, description = "인기글 보상: ${post.title}"))
            }
        }

        // 5. 모든 멤버에게 균등 분배
        val members = userMembershipRepository.findAll().filter { it.membership.worldview.id == worldviewId }.map { it.user }.distinct()
        if (members.isNotEmpty()) {
            val individualMemberShare = membersShare.divide(members.size.toBigDecimal(), 10, RoundingMode.HALF_UP)
            members.forEach { member ->
                member.balance = member.balance.add(individualMemberShare)
                userRepository.save(member)
                revenueShareRepository.save(RevenueShare(history = history, user = member, amount = individualMemberShare, description = "멤버십 참여 수익"))
            }
        }

        // 6. 세계관 수익 풀 초기화
        worldview.revenuePool = BigDecimal.ZERO
        worldviewRepository.save(worldview)
    }

    // ✅ 관리자 페이지에서 분배 내역을 보기 위한 메소드 추가
    @Transactional(readOnly = true)
    fun getDistributionHistory(worldviewId: Long, userEmail: String): List<DistributionHistory> {
        val worldview = worldviewRepository.findById(worldviewId)
            .orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }
        if (worldview.creator.email != userEmail) {
            throw AccessDeniedException("분배 내역을 조회할 권한이 없습니다.")
        }
        return distributionHistoryRepository.findByWorldviewIdOrderByIdDesc(worldviewId)
    }
    // 공동 창작자 초대
    fun addContributor(worldviewId: Long, userEmail: String, request: ContributorRequest): ContributorResponse {
        val worldview = worldviewRepository.findById(worldviewId)
            .orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        if (worldview.creator.email != userEmail) {
            throw AccessDeniedException("세계관 소유자만 공동 창작자를 초대할 수 있습니다.")
        }

        val contributorUser = userRepository.findByEmail(request.userEmail)
            ?: throw EntityNotFoundException("초대할 사용자를 찾을 수 없습니다.")

        if (contributorRepository.findByWorldViewIdAndUserId(worldviewId, contributorUser.id!!) != null) {
            throw IllegalStateException("이미 추가된 공동 창작자입니다.")
        }

        val contributor = Contributor(
            user = contributorUser,
            worldView = worldview,
            permission = request.permission
        )

        return ContributorResponse.from(contributorRepository.save(contributor))
    }

    // 공동 창작자 목록 조회
    @Transactional(readOnly = true)
    fun getContributors(worldviewId: Long): List<ContributorResponse> {
        return contributorRepository.findByWorldViewId(worldviewId).map { ContributorResponse.from(it) }
    }
}