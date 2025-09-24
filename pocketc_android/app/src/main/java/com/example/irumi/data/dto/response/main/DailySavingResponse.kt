package com.example.irumi.data.dto.response.main

import kotlinx.serialization.Serializable

@Serializable
data class DailySavingResponse(
    val savingScore: Int,
    val totalSpending: Int
)
