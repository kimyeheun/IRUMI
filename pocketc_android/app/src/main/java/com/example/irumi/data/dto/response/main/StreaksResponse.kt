package com.example.irumi.data.dto.response.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreaksResponse(
    @SerialName("streaks") val streaks: List<StreakResponse>
)

@Serializable
data class StreakResponse(
    @SerialName("date") val date: String,              // "2025-09-25"
    @SerialName("missionsCompleted") val missionsCompleted: Int,
    @SerialName("spending") val spending: Long,
    @SerialName("isActive") val isActive: Boolean
)


