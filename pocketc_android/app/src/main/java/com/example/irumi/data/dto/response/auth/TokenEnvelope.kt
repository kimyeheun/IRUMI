package com.example.irumi.data.dto.response.auth

import com.example.irumi.data.dto.response.auth.TokenPair
import kotlinx.serialization.Serializable

@Serializable
data class TokenEnvelope(
    val accessToken: String,
    val refreshToken: String
)
