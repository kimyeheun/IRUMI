package com.example.irumi.domain.entity

data class EventEntity(
    val eventId: Int,
    val eventName: String,
    val eventDescription: String,
    val eventImageUrl: String,
    val badgeName: String,
    val badgeDescription: String,
    val startAt: String,
    val endAt: String
)