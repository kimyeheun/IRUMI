package com.example.irumi.domain.entity.main

data class BadgeEntity(
    val badgeId: Int,
    val badgeName: String,
    val badgeDescription: String,
    val level: Int,
    val badgeImageUrl: String,
    val createdAt: String
)
