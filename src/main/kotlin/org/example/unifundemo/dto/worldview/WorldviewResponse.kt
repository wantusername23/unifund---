package org.example.unifundemo.dto.worldview

import org.example.unifundemo.domain.worldview.WorldView

// 목록 조회를 위한 간단한 정보 DTO
data class WorldviewSimpleResponse(
    val id: Long,
    val name: String,
    val coverImageUrl: String,
    val creatorNickname: String,
    val isCreator: Boolean
) {
    companion object {
        fun from(worldview: WorldView, isCreator: Boolean): WorldviewSimpleResponse {
            return WorldviewSimpleResponse(
                id = worldview.id!!,
                name = worldview.name,
                coverImageUrl = worldview.coverImageUrl,
                creatorNickname = worldview.creator.nickname,
                isCreator = isCreator
            )
        }
    }
}

// 상세 조회를 위한 모든 정보 DTO
data class WorldviewDetailResponse(
    val id: Long,
    val name: String,
    val description: String,
    val keywords: String,
    val coverImageUrl: String,
    val creatorNickname: String,
    val isCreator: Boolean
) {
    companion object {
        fun from(worldview:WorldView, isCreator: Boolean): WorldviewDetailResponse {
            return WorldviewDetailResponse(
                id = worldview.id!!,
                name = worldview.name,
                description = worldview.description,
                keywords = worldview.keywords,
                coverImageUrl = worldview.coverImageUrl,
                creatorNickname = worldview.creator.nickname,
                isCreator = isCreator
            )
        }
    }
}