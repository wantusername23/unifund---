package org.example.unifundemo.service

import jakarta.persistence.EntityNotFoundException
import org.example.unifundemo.domain.worldview.Permission
import org.example.unifundemo.dto.worldview.*
import org.example.unifundemo.repository.*
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.example.unifundemo.domain.worldview.Character
import org.example.unifundemo.domain.worldview.TimelineEvent

@Service
@Transactional
class WorldviewSubService(
    private val worldviewRepository: WorldViewRepository,
    private val characterRepository: CharacterRepository,
    private val timelineEventRepository: TimelineEventRepository,
    private val contributorRepository: ContributorRepository,
    private val userRepository: UserRepository
) {
    // ============== Character Service Logic ==============

    fun createCharacter(worldviewId: Long, userEmail: String, request: CharacterRequest): CharacterResponse {
        checkEditPermission(worldviewId, userEmail)
        val worldview = worldviewRepository.getReferenceById(worldviewId)
        val character = Character(
            name = request.name,
            description = request.description,
            relationships = request.relationships,
            worldView = worldview
        )
        return CharacterResponse.from(characterRepository.save(character))
    }

    @Transactional(readOnly = true)
    fun getCharacters(worldviewId: Long): List<CharacterResponse> {
        return characterRepository.findByWorldViewId(worldviewId).map { CharacterResponse.from(it) }
    }

    // ============== Timeline Service Logic ==============

    fun createTimelineEvent(worldviewId: Long, userEmail: String, request: TimelineEventRequest): TimelineEventResponse {
        checkEditPermission(worldviewId, userEmail)
        val worldview = worldviewRepository.getReferenceById(worldviewId)
        val event = TimelineEvent(
            title = request.title,
            description = request.description,
            eventDate = request.eventDate,
            worldView = worldview
        )
        return TimelineEventResponse.from(timelineEventRepository.save(event))
    }

    @Transactional(readOnly = true)
    fun getTimelineEvents(worldviewId: Long): List<TimelineEventResponse> {
        return timelineEventRepository.findByWorldViewIdOrderByCreatedAtAsc(worldviewId).map { TimelineEventResponse.from(it) }
    }

    // ============== Helper Function for Permission Check ==============

    private fun checkEditPermission(worldviewId: Long, userEmail: String) {
        val user = userRepository.findByEmail(userEmail) ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        val worldview = worldviewRepository.findById(worldviewId)
            .orElseThrow { EntityNotFoundException("세계관을 찾을 수 없습니다.") }

        val isCreator = worldview.creator.id == user.id
        val isEditor = contributorRepository.findByWorldViewIdAndUserId(worldviewId, user.id!!)
            ?.let { it.permission == Permission.EDITOR } ?: false

        if (!isCreator && !isEditor) {
            throw AccessDeniedException("이 작업을 수행할 권한이 없습니다.")
        }
    }
}