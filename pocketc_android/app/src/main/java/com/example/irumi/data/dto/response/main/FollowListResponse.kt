package com.example.irumi.data.dto.response.main

import kotlinx.serialization.Serializable

@Serializable
data class FollowListResponse(
    val follows: List<FollowDto>
) {
    @Serializable
    data class FollowDto(
        val followUserId: Int,
        val nickname: String,
        val profileImageUrl: String
    )
}
