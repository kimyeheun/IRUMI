package com.example.irumi.data.dto.response.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MissionsResponse(
    @SerialName("missionReceived") val missionReceived: Boolean,
    @SerialName("missions") val missions: List<MissionResponse>
)

@Serializable
data class MissionResponse(
    @SerialName("missionId") val missionId: Int,
    @SerialName("subId") val subId: Int,
    @SerialName("type") val type: Int,
    @SerialName("mission") val mission: String,
    @SerialName("status") val status: String,
    @SerialName("progress") val progress: Int,
    @SerialName("value") val value: Int,
    @SerialName("template") val template: String
)
