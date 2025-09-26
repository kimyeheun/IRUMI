package com.example.irumi.domain.entity.main

data class MissionEntity(
    val mission: String,
    val subId: Int,
    val dsl: String,
    val type: Int,
    val validFrom: String,
    val validTo: String
)

data class MissionsEntity(
    val userId: Int,
    val date: String,
    val missions: List<MissionEntity>
)
