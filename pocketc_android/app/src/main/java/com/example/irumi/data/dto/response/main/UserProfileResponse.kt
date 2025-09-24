// data/dto/response/main/UserProfileResponse.kt
package com.example.irumi.data.dto.response.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    @SerialName("userId") val userId: Int,
    @SerialName("name") val name: String,
    @SerialName("budget") val budget: Long, // 서버 숫자 타입에 맞춰 Int/Long 결정
    @SerialName("profileImageUrl") val profileImageUrl: String
)
