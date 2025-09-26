package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.main.MissionsSelectRequest
import com.example.irumi.data.dto.response.main.*
import retrofit2.http.*

interface MainService {
    @GET("me") suspend fun getUserProfile(): BaseResponse<UserProfileResponse>
    @GET("daily") suspend fun getDailySaving(): BaseResponse<DailySavingResponse>

    @GET("follows") suspend fun getFollowIds(): BaseResponse<FollowIdsResponse>
    @GET("badges") suspend fun getBadges(): BaseResponse<BadgeListResponse>
    @GET("streaks") suspend fun getStreaks(): BaseResponse<StreaksResponse>

    @POST("follows/{targetUserId}") suspend fun postFollow(@Path("targetUserId") id: Int): BaseResponse<Unit?>
    @DELETE("follows/{targetUserId}") suspend fun deleteFollow(@Path("targetUserId") id: Int): BaseResponse<Unit?>

    @GET("daily/{friendId}") suspend fun getDailyWithFriend(@Path("friendId") id: Int): BaseResponse<FriendDailyResponse>

    // ✅ 단일 엔드포인트
    @GET("missions") suspend fun getMissions(): BaseResponse<MissionsResponse>
    @POST("missions") suspend fun postMissions(@Body body: MissionsSelectRequest): BaseResponse<MissionsResponse>
}
