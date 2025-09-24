package com.example.irumi.data.dto.response.main

import kotlinx.serialization.Serializable

@Serializable
data class BadgeListResponse(
    val badges: List<BadgeDto>
) {
    @Serializable
    data class BadgeDto(
        val badgeId: Int,
        val badgeName: String,
        val badgeDescription: String,
        val level: Int,
        val badgeImageUrl: String,
        val createdAt: String
    )
}
