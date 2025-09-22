package com.example.irumi.data.mapper

import com.example.irumi.data.dto.response.auth.MemberProfileResponse
import com.example.irumi.domain.entity.MemberProfileEntity

// DTO → Domain
fun MemberProfileResponse.toEntity() = MemberProfileEntity(
    profileImage = profileImage,
    name = name,
    email = email,
    password = password,
    budget = budget
)