package com.example.irumi.data.dto.response.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowIdsResponse(
    @SerialName("follows") val follows: List<FollowItem>
)

@Serializable
data class FollowItem(
    @SerialName("followeeId") val followUserId: Int,  // ← 프로퍼티명: followUserId
    @SerialName("followedAt") val followedAt: String
)

