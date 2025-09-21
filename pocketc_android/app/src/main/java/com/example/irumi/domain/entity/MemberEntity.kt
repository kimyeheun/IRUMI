package com.example.irumi.domain.entity

data class MemberEntity(
    val userId: Int,
    val name: String,
    val profileImageUrl: String,
    val isFriend: Boolean
)