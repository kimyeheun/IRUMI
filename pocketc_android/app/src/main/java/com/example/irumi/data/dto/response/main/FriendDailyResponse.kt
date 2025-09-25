package com.example.irumi.data.dto.response.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendDailyResponse(
    @SerialName("me")     val me: DailySavingResponse,
    @SerialName("friend") val friend: DailySavingResponse
)
