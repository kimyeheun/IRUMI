package com.example.irumi.domain.entity.main

data class MissionEntity(
    val missionId: Int,
    val subId: Int,
    val type: Int,
    val mission: String,
    val status: String,
    val progress: Int? = 0,
    val value: Int? = 0,
    val template: String? = ""
)

data class MissionsEntity(
    val missionReceived: Boolean,
    val missions: List<MissionEntity>
)
