package com.example.irumi.domain.entity.main

data class UserProfileEntity(
    val userId: Int,
    val name: String,
    val budget: Int,
    val profileImageUrl: String
)
