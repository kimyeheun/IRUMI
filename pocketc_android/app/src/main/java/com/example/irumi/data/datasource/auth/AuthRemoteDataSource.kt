package com.example.irumi.data.datasource.auth

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.auth.AuthEditRequest
import com.example.irumi.data.dto.request.auth.LoginRequest
import com.example.irumi.data.dto.request.auth.SignUpRequest
import com.example.irumi.data.dto.response.auth.LoginEnvelope
import com.example.irumi.data.dto.response.auth.MemberProfileResponse
import com.example.irumi.data.dto.response.auth.TokenEnvelope
import com.example.irumi.data.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val service: AuthService
) : AuthDataSource {
    override suspend fun signUp(body: SignUpRequest): BaseResponse<TokenEnvelope> =
        service.signUp(body)

    override suspend fun login(body: LoginRequest): BaseResponse<LoginEnvelope> =
        service.login(body)

    override suspend fun logout(): BaseResponse<Unit?> =
        service.logout()

    override suspend fun reissue(): BaseResponse<TokenEnvelope> =
        service.reissue()

    override suspend fun updateMe(body: AuthEditRequest): BaseResponse<MemberProfileResponse> =
        service.updateMe(body)
}
