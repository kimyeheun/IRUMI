package com.example.irumi.data.dto.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class MemberProfileResponse(
    val profileImage: String? = null,
    val name: String,
    val email: String,
    val password: String,
    val budget: Int
)
