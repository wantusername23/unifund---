package org.example.unifundemo.service

import org.example.unifundemo.domain.tag.Tag
import org.example.unifundemo.repository.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TagService(private val tagRepository: TagRepository) {

    fun findOrCreateTags(tagNames: Set<String>): Set<Tag> {
        val existingTags = tagRepository.findByNameIn(tagNames).toMutableSet()
        val existingTagNames = existingTags.map { it.name }.toSet()
        val newTagNames = tagNames - existingTagNames

        val newTags = newTagNames.map { Tag(name = it) }.toSet()
        tagRepository.saveAll(newTags)

        existingTags.addAll(newTags)
        return existingTags
    }
}