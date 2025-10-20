package org.example.unifundemo.service

import org.example.unifundemo.dto.profile.ProfileResponse
import org.example.unifundemo.dto.profile.WorldviewInfo
import org.example.unifundemo.repository.PostRepository
import org.example.unifundemo.repository.UserRepository
import org.example.unifundemo.repository.WorldViewRepository
import jakarta.persistence.EntityNotFoundException
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
            participatedWorldviews = participatedWorldviews
        )
    }

    fun toggleBalanceVisibility(userEmail: String): Boolean {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        user.showBalance = !user.showBalance
        userRepository.save(user)
        return user.showBalance
    }
}