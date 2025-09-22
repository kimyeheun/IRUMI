package com.example.irumi.data.datasource.auth

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.auth.AuthEditRequest
import com.example.irumi.data.dto.request.auth.LoginRequest
import com.example.irumi.data.dto.request.auth.SignUpRequest
import com.example.irumi.data.dto.response.auth.LoginEnvelope
import com.example.irumi.data.dto.response.auth.MemberProfileResponse
import com.example.irumi.data.dto.response.auth.TokenEnvelope
import com.example.irumi.data.dto.response.auth.TokenPair
import javax.inject.Inject

private val AuthEditRequest.budget: Int
    get() {
        TODO()
    }
private val AuthEditRequest.password: String
    get() {
        TODO()
    }
private val AuthEditRequest.email: String
    get() {
        TODO()
    }
private val AuthEditRequest.name: String
    get() {
        TODO()
    }

class AuthLocalDataSource @Inject constructor() : AuthDataSource {
    override suspend fun signUp(body: SignUpRequest) =
        BaseResponse(status = 201, message = "회원가입이 완료되었습니다",
            data = TokenEnvelope(TokenPair("access_dummy", "refresh_dummy"))
        )

    override suspend fun login(body: LoginRequest) =
        BaseResponse(status = 201, message = "로그인이 완료되었습니다",
            data = LoginEnvelope(token = "dummy_token")
        )

    override suspend fun logout() =
        BaseResponse(status = 200, message = "로그아웃이 완료되었습니다", data = null)

    override suspend fun reissue() =
        BaseResponse(status = 201, message = "토큰 발급이완료되었습니다",
            data = TokenEnvelope(TokenPair("new_access","new_refresh")))

    override suspend fun updateMe(body: AuthEditRequest) =
        BaseResponse(status = 200, message = "회원정보가 수정되었습니다",
            data = MemberProfileResponse(null, body.name, body.email, body.password, body.budget)
        )
}
