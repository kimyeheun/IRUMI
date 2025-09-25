package com.example.irumi.data.dto.request.main

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MissionsSelectRequest(
    @SerialName("selected") val selected: List<Int>
)
