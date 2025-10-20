package org.example.unifundemo.repository

import org.example.unifundemo.domain.worldview.Character
import org.springframework.data.jpa.repository.JpaRepository

interface CharacterRepository : JpaRepository<Character, Long> {
    fun findByWorldViewId(worldViewId: Long): List<Character>
}