package com.example.irumi.data.dto.response.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MissionsResponse(
    @SerialName("userId") val userId: Int,
    @SerialName("date") val date: String,              // "2025-09-26"
    @SerialName("missions") val missions: List<MissionResponse>
)

@Serializable
data class MissionResponse(
    @SerialName("mission") val mission: String,        // 사람 읽는 설명
    @SerialName("subId") val subId: Int,               // 세부 과제 식별자
    @SerialName("dsl") val dsl: String,                // 서버용 DSL 원문
    @SerialName("type") val type: Int,                 // 0=daily, 1=weekly, 2=monthly (예시)
    @SerialName("validFrom") val validFrom: String,    // ISO-8601
    @SerialName("validTo") val validTo: String         // ISO-8601
)
