// data/service/MainService.kt
package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.core.state.UiState
import com.example.irumi.data.dto.response.main.*
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MainService {
    @GET("me") suspend fun getUserProfile(): BaseResponse<UserProfileResponse>
    @GET("daily") suspend fun getDailySaving(): BaseResponse<DailySavingResponse>

    // 사용 안 함: @GET("spending") suspend fun getSpending(): BaseResponse<SpendingResponse>

    // 닉/이미지 없는 스키마 → getFollows 제거하고 아래만 유지
    @GET("follows") suspend fun getFollowIds(): BaseResponse<FollowIdsResponse>

    @GET("badges") suspend fun getBadges(): BaseResponse<BadgeListResponse>
    @GET("streaks") suspend fun getStreaks(): BaseResponse<StreaksResponse>

    @POST("follows/{targetUserId}") suspend fun postFollow(@Path("targetUserId") id: Int): BaseResponse<Unit?>
    @DELETE("follows/{targetUserId}") suspend fun deleteFollow(@Path("targetUserId") id: Int): BaseResponse<Unit?>
}
