package org.example.unifundemo.dto.worldview

import jakarta.validation.constraints.NotBlank
import org.example.unifundemo.domain.worldview.Character

data class CharacterRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val description: String,
    val relationships: String
)

data class CharacterResponse(
    val id: Long,
    val name: String,
    val description: String,
    val relationships: String
) {
    companion object {
        fun from(character: Character) = CharacterResponse(
            id = character.id!!,
            name = character.name,
            description = character.description,
            relationships = character.relationships
        )
    }
}