package com.example.irumi.data.dto.response.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowIdsResponse(
    @SerialName("follows") val follows: List<FollowItem>
)

@Serializable
data class FollowItem(
    @SerialName("followeeId") val followUserId: Int,
    @SerialName("followedAt") val followedAt: String
)
