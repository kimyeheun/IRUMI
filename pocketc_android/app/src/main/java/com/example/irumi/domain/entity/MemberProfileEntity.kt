package com.example.irumi.domain.entity

data class MemberProfileEntity(
    val profileImage: String?,
    val name: String,
    val email: String,
    val password: String,
    val budget: Int
)
