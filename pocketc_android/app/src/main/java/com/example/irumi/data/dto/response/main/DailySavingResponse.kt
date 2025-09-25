package com.example.irumi.data.dto.response.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DailySavingResponse
@Serializable
data class DailySavingResponse(
    @SerialName("savingScore") val savingScore: Int,
    @SerialName("totalSpending") val totalSpending: Int
)
