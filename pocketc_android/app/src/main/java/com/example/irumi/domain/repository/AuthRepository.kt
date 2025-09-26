package com.example.irumi.domain.repository

import com.example.irumi.data.dto.request.auth.AuthEditRequest
import com.example.irumi.data.dto.request.auth.LoginRequest
import com.example.irumi.data.dto.request.auth.SignUpRequest
import com.example.irumi.data.dto.response.auth.LoginEnvelope
import com.example.irumi.data.dto.response.auth.TokenEnvelope
import com.example.irumi.domain.entity.BaseEntity
import com.example.irumi.domain.entity.MemberProfileEntity

interface AuthRepository {
    suspend fun signUp(req: SignUpRequest): Result<BaseEntity<LoginEnvelope>>
    suspend fun login(req: LoginRequest): Result<LoginEnvelope>
    suspend fun logout(): Result<Unit>
    suspend fun reissue(): Result<TokenEnvelope>
    suspend fun updateMe(req: AuthEditRequest): Result<MemberProfileEntity>
}
