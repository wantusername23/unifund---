package org.example.unifundemo.controller

import org.example.unifundemo.repository.TagRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tags")
class TagController(private val tagRepository: TagRepository) {

    @GetMapping
    fun getAllTags(): ResponseEntity<List<String>> {
        val tags = tagRepository.findAll().map { it.name }
        return ResponseEntity.ok(tags)
    }
}