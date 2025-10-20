package org.example.unifundemo.controller

import org.example.unifundemo.dto.worldview.*
import org.example.unifundemo.service.WorldviewSubService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/worldviews/{worldviewId}")
class WorldviewSubController(
    private val subService: WorldviewSubService
) {
    // ============== Character Endpoints ==============
    @PostMapping("/characters")
    fun createCharacter(
        @PathVariable worldviewId: Long,
        principal: Principal,
        @RequestBody request: CharacterRequest
    ): ResponseEntity<CharacterResponse> {
        val character = subService.createCharacter(worldviewId, principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(character)
    }

    @GetMapping("/characters")
    fun getCharacters(@PathVariable worldviewId: Long): ResponseEntity<List<CharacterResponse>> {
        return ResponseEntity.ok(subService.getCharacters(worldviewId))
    }

    // ============== Timeline Endpoints ==============
    @PostMapping("/timeline")
    fun createTimelineEvent(
        @PathVariable worldviewId: Long,
        principal: Principal,
        @RequestBody request: TimelineEventRequest
    ): ResponseEntity<TimelineEventResponse> {
        val event = subService.createTimelineEvent(worldviewId, principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(event)
    }

    @GetMapping("/timeline")
    fun getTimelineEvents(@PathVariable worldviewId: Long): ResponseEntity<List<TimelineEventResponse>> {
        return ResponseEntity.ok(subService.getTimelineEvents(worldviewId))
    }
}