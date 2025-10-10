package com.example.irumi.domain.entity.main

data class StreakEntity(
    val date: String,              // yyyy-MM-dd
    val missionsCompleted: Int,
    val spending: Long,
    val isActive: Boolean
)
