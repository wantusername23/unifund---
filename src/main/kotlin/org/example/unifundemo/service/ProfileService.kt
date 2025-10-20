package org.example.unifundemo.service

import jakarta.persistence.EntityNotFoundException
import org.example.unifundemo.dto.profile.ProfileResponse
import org.example.unifundemo.dto.profile.UpdateProfileRequest
import org.example.unifundemo.dto.profile.WorldviewInfo
import org.example.unifundemo.repository.PostRepository
import org.example.unifundemo.repository.UserRepository
import org.example.unifundemo.repository.WorldViewRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProfileService(
    private val userRepository: UserRepository,
    private val worldviewRepository: WorldViewRepository,
    private val postRepository: PostRepository
) {
    @Transactional(readOnly = true)
    fun getUserProfile(userEmail: String): ProfileResponse {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")

        val createdWorldviews = worldviewRepository.findByCreatorIdOrderByIdDesc(user.id!!)
            .map { WorldviewInfo(it.id!!, it.name, it.coverImageUrl) }

        val participatedWorldviews = postRepository.findDistinctWorldviewByAuthor(user)
            .map { WorldviewInfo(it.id!!, it.name, it.coverImageUrl) }

        return ProfileResponse(
            nickname = user.nickname,
            balance = user.balance,
            showBalance = user.showBalance,
            createdWorldviews = createdWorldviews,
            participatedWorldviews = participatedWorldviews,
            // ✅ 새로 추가된 정보 매핑
            bio = user.bio,
            socialMediaLink = user.socialMediaLink,
            representativeWorldview = user.representativeWorldview?.let {
                WorldviewInfo(it.id!!, it.name, it.coverImageUrl)
            }
        )
    }

    // ✅ 프로필 업데이트 메서드 추가
    fun updateUserProfile(userEmail: String, request: UpdateProfileRequest): ProfileResponse {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")

        user.nickname = request.nickname
        user.bio = request.bio
        user.socialMediaLink = request.socialMediaLink

        // 대표 세계관 설정
        if (request.representativeWorldviewId == null) {
            user.representativeWorldview = null
        } else {
            val worldview = worldviewRepository.findById(request.representativeWorldviewId)
                .orElseThrow { EntityNotFoundException("대표 세계관으로 설정할 세계관을 찾을 수 없습니다.") }

            // 본인이 생성한 세계관만 대표 세계관으로 설정 가능
            if (worldview.creator.id != user.id) {
                throw AccessDeniedException("자신이 생성한 세계관만 대표로 설정할 수 있습니다.")
            }
            user.representativeWorldview = worldview
        }

        userRepository.save(user)
        return getUserProfile(userEmail) // 업데이트된 정보로 다시 조회하여 반환
    }


    fun toggleBalanceVisibility(userEmail: String): Boolean {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        user.showBalance = !user.showBalance
        userRepository.save(user)
        return user.showBalance
    }
}