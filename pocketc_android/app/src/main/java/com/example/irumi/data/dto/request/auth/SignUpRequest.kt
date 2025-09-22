package com.example.irumi.data.dto.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    // val profileImageUrl: String? = null, // S3 오픈 전 제외
    val budget: Int
)