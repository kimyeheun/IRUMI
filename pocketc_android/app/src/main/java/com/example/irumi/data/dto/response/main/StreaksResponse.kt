package com.example.irumi.data.dto.response.main

import kotlinx.serialization.Serializable

@Serializable
data class StreaksResponse(
    val streaks: List<StreakDto>
) {
    @Serializable
    data class StreakDto(
        val date: String,
        val missionsCompleted: Int,
        val spending: Int,
        val isActive: Boolean
    )
}
