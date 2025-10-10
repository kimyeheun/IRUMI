package com.example.irumi.data.dto.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val budget: Int
)