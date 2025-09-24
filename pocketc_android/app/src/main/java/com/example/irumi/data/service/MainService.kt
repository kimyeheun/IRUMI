package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.main.*
import retrofit2.http.GET

interface MainService {
    @GET("users/me")
    suspend fun getUserProfile(): BaseResponse<UserProfileResponse>

    @GET("users/daily")
    suspend fun getDailySaving(): BaseResponse<DailySavingResponse>

    @GET("users/spending")
    suspend fun getSpending(): BaseResponse<SpendingResponse>

    @GET("users/follows")
    suspend fun getFollows(): BaseResponse<FollowListResponse>

    @GET("users/badges")
    suspend fun getBadges(): BaseResponse<BadgeListResponse>

    @GET("users/streaks")
    suspend fun getStreaks(): BaseResponse<StreaksResponse>
}
