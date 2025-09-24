package com.example.irumi.data.dto.response.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BadgeListResponse(
    @SerialName("badges") val badges: List<BadgeResponse>
)

@Serializable
data class BadgeResponse(
    @SerialName("badgeId") val badgeId: Int,
    @SerialName("badgeName") val badgeName: String,
    @SerialName("badgeDescription") val badgeDescription: String,
    @SerialName("level") val level: Int,
    @SerialName("badgeImageUrl") val badgeImageUrl: String,
    @SerialName("createdAt") val createdAt: String
)
