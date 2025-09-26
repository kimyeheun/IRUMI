package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.main.MissionsSelectRequest
import com.example.irumi.data.dto.response.main.*
import retrofit2.http.*

interface MainService {
    @GET("me")
    suspend fun getUserProfile(): BaseResponse<UserProfileResponse>

    @GET("daily")
    suspend fun getDailySaving(): BaseResponse<DailySavingResponse>

    @GET("follows")
    suspend fun getFollowIds(): BaseResponse<FollowIdsResponse>

    @GET("badges")
    suspend fun getBadges(): BaseResponse<BadgeListResponse>

    @GET("streaks")
    suspend fun getStreaks(): BaseResponse<StreaksResponse>

    @POST("follows/{targetUserId}")
    suspend fun postFollow(@Path("targetUserId") id: Int): BaseResponse<Unit?>

    @DELETE("follows/{targetUserId}")
    suspend fun deleteFollow(@Path("targetUserId") id: Int): BaseResponse<Unit?>

    @GET("daily/{friendId}")
    suspend fun getDailyWithFriend(@Path("friendId") id: Int): BaseResponse<FriendDailyResponse>

    // ✅ 미션 API (절대 경로 사용!)
    @GET("/api/v1/ai/missions/{userId}/daily")
    suspend fun getDailyMissions(@Path("userId") userId: Int): BaseResponse<MissionsResponse>

    @GET("/api/v1/ai/missions/{userId}/weekly")
    suspend fun getWeeklyMissions(@Path("userId") userId: Int): BaseResponse<MissionsResponse>

    @GET("/api/v1/ai/missions/{userId}/monthly")
    suspend fun getMonthlyMissions(@Path("userId") userId: Int): BaseResponse<MissionsResponse>

    // 선택 제출
    @POST("/api/v1/ai/missions")
    suspend fun postMissions(@Body body: MissionsSelectRequest): BaseResponse<MissionsResponse>
}
