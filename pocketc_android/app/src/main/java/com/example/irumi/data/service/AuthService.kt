package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.auth.LoginRequest
import com.example.irumi.data.dto.request.auth.AuthEditRequest
import com.example.irumi.data.dto.response.auth.LoginEnvelope
import com.example.irumi.data.dto.response.auth.MemberProfileResponse
import com.example.irumi.data.dto.response.auth.TokenEnvelope
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthService {

    /** 회원가입 */
    @POST("../users")
    suspend fun signUp(
        @Body body: com.example.irumi.data.dto.request.auth.SignUpRequest
    ): BaseResponse<TokenEnvelope>

    /** 로그인 */
    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): BaseResponse<LoginEnvelope>

    /** 로그아웃 */
    @POST("logout")
    suspend fun logout(): BaseResponse<Unit?> // 서버가 data 비워서 내려줘도 OK

    /** 토큰 재발급 */
    @POST("reissue")
    suspend fun reissue(): BaseResponse<TokenEnvelope>

    /** 회원정보 수정 */
    @PATCH("me")
    suspend fun updateMe(
        @Body body: AuthEditRequest
    ): BaseResponse<MemberProfileResponse>
}
