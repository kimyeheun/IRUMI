package com.example.irumi.data.dto.response.main

import kotlinx.serialization.Serializable

@Serializable
data class FollowIdsResponse(
    val follows: List<FollowEntry>
) {
    @Serializable
    data class FollowEntry(
        val followeeId: Int,
        val followedAt: String
    )
}
