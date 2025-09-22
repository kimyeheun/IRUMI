package com.example.irumi.data.dto.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginEnvelope(
    val token: String
)
