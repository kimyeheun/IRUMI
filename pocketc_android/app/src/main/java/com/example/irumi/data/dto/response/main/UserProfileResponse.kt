package com.example.irumi.data.dto.response.main

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val userId: Int,
    val name: String,
    val budget: String,
    val profileImageUrl: String
)
