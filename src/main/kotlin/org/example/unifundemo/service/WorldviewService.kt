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

@Service
@Transactional
class WorldviewService(
    private val worldviewRepository: WorldViewRepository,
    private val userRepository: UserRepository,
    private val membershipRepository: MembershipRepository,
    private val userMembershipRepository: UserMembershipRepository
) {
    fun createWorldview(email: String, request: CreateWorldviewRequest): WorldView {
        // 1. 이메일을 사용해서 창조자(User) 정보를 찾습니다.
        val creator = userRepository.findByEmail(email) ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

        // 2. DTO와 창조자 정보를 바탕으로 Worldview 객체를 만듭니다.
        val worldview = WorldView(
            name = request.name,
            description = request.description,
            keywords = request.keywords,
            coverImageUrl = request.coverImageUrl,
            creator = creator
        )

        // 3. 데이터베이스에 저장하고 반환합니다.
        return worldviewRepository.save(worldview)
    }
    // 전체 세계관 목록 조회 메소드 추가
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션은 성능에 이점이 있음
    fun getAllWorldviews(): List<WorldviewSimpleResponse> {
        return worldviewRepository.findAll()
            .map { worldview -> WorldviewSimpleResponse.from(worldview) }
    }

    // 특정 세계관 상세 조회 메소드 추가
    @Transactional(readOnly = true)
    fun getWorldviewById(id: Long): WorldviewDetailResponse {
        val worldview = worldviewRepository.findById(id)
            .orElseThrow { EntityNotFoundException("해당 ID의 세계관을 찾을 수 없습니다: $id") }
        return WorldviewDetailResponse.from(worldview)
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

        // 비즈니스 로직: 이미 가입한 멤버십인지 확인
        if (userMembershipRepository.existsByUserAndMembershipId(user, membershipId)) {
            throw IllegalStateException("이미 가입한 멤버십입니다.")
        }

        // 💡 실제 서비스에서는 이 부분에서 결제 API를 호출해야 합니다.
        // paymentGateway.processPayment(user, membership.price)

        val userMembership = UserMembership(
            user = user,
            membership = membership
        )

        userMembershipRepository.save(userMembership)
    }
    @Transactional(readOnly = true)
    fun searchWorldviews(query: String): List<WorldviewSimpleResponse> {
        return worldviewRepository.findByNameContainingIgnoreCaseOrKeywordsContainingIgnoreCase(query, query)
            .map { worldview -> WorldviewSimpleResponse.from(worldview) }
    }
}