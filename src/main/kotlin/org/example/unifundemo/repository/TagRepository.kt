package org.example.unifundemo.repository

import org.example.unifundemo.domain.tag.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {
    fun findByNameIn(names: Set<String>): Set<Tag>
    fun findByName(name: String): Tag?
}