package com.example.irumi.data.datasource.auth

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.auth.AuthEditRequest
import com.example.irumi.data.dto.request.auth.LoginRequest
import com.example.irumi.data.dto.request.auth.SignUpRequest
import com.example.irumi.data.dto.response.auth.LoginEnvelope
import com.example.irumi.data.dto.response.auth.MemberProfileResponse
import com.example.irumi.data.dto.response.auth.TokenEnvelope

interface AuthDataSource {
    suspend fun signUp(body: SignUpRequest): BaseResponse<LoginEnvelope>
    suspend fun login(body: LoginRequest): BaseResponse<LoginEnvelope>
    suspend fun logout(): BaseResponse<Unit?>
    suspend fun reissue(): BaseResponse<TokenEnvelope>
    suspend fun updateMe(body: AuthEditRequest): BaseResponse<MemberProfileResponse>

    suspend fun postAiTransaction(): BaseResponse<Unit>
}
