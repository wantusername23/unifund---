package org.example.unifundemo.controller

import org.example.unifundemo.dto.profile.ProfileResponse
import org.example.unifundemo.dto.profile.UpdateProfileRequest
import org.example.unifundemo.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/profile")
class ProfileController(
    private val profileService: ProfileService
) {
    @GetMapping
    fun getProfile(principal: Principal): ResponseEntity<ProfileResponse> {
        val profile = profileService.getUserProfile(principal.name)
        return ResponseEntity.ok(profile)
    }

    // ✅ 프로필 업데이트 엔드포인트 추가
    @PutMapping
    fun updateProfile(principal: Principal, @RequestBody request: UpdateProfileRequest): ResponseEntity<ProfileResponse> {
        val updatedProfile = profileService.updateUserProfile(principal.name, request)
        return ResponseEntity.ok(updatedProfile)
    }

    @PatchMapping("/toggle-balance")
    fun toggleBalanceVisibility(principal: Principal): ResponseEntity<Map<String, Boolean>> {
        val newVisibility = profileService.toggleBalanceVisibility(principal.name)
        return ResponseEntity.ok(mapOf("showBalance" to newVisibility))
    }
}