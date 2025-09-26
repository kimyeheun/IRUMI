package com.example.irumi.domain.entity.main

data class FollowInfoEntity(
    val followUserId: Int,
    val followedAt: String,
    val followeeName: String,
    val followeeProfile: String
)

