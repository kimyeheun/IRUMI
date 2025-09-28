package com.example.irumi.data.dto.request.main

import kotlinx.serialization.Serializable

@Serializable
data class FollowRequest(
    val userCode: String
)
