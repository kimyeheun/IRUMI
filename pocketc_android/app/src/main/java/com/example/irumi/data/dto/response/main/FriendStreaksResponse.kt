package com.example.irumi.data.dto.response.main

import kotlinx.serialization.Serializable

@Serializable
data class FriendStreaksResponse(
    val friendName: String,
    val friendStreak: FriendStreak
)

@Serializable
data class FriendStreak(
    val streaks: List<StreakResponse>
)
