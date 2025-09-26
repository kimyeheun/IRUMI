package com.example.irumi.data.dto.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthEditRequest(
    val name: String,
    val profileImageUrl: String,
    val budget: Int
)
