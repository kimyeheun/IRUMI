package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.main.FollowRequest
import com.example.irumi.data.dto.request.main.MissionsSelectRequest
import com.example.irumi.data.dto.response.main.BadgeListResponse
import com.example.irumi.data.dto.response.main.DailySavingResponse
import com.example.irumi.data.dto.response.main.FollowIdsResponse
import com.example.irumi.data.dto.response.main.FriendDailyResponse
import com.example.irumi.data.dto.response.main.FriendStreaksResponse
import com.example.irumi.data.dto.response.main.MissionsResponse
import com.example.irumi.data.dto.response.main.StreaksResponse
import com.example.irumi.data.dto.response.main.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MainService {
    @GET("me") suspend fun getUserProfile(): BaseResponse<UserProfileResponse>
    @GET("daily") suspend fun getDailySaving(): BaseResponse<DailySavingResponse>

    @GET("follows") suspend fun getFollowIds(): BaseResponse<FollowIdsResponse>
    @GET("badges") suspend fun getBadges(): BaseResponse<BadgeListResponse>
    @GET("streaks") suspend fun getStreaks(): BaseResponse<StreaksResponse>

    @POST("follows/{targetUserId}") suspend fun postFollow(@Path("targetUserId") id: Int): BaseResponse<Unit?>
    @POST("follows")
    suspend fun postFollowByUserCode(@Body body: FollowRequest): BaseResponse<Unit?>

    @DELETE("follows/{targetUserId}") suspend fun deleteFollow(@Path("targetUserId") id: Int): BaseResponse<Unit?>

    /**
     * 친구 이름과 스트릭 조회
     */
    @GET("streaks/{friendId}")
    suspend fun getStreaksWithFriend(
        @Path("friendId") friendId: Int
    ): BaseResponse<FriendStreaksResponse>

    /**
     * 친구 배지 조회
     */
    @GET("badges/{friendId}")
    suspend fun getBadgesWithFriend(
        @Path("friendId") friendId: Int
    ): BaseResponse<BadgeListResponse>

    @GET("daily/{friendId}") suspend fun getDailyWithFriend(@Path("friendId") id: Int): BaseResponse<FriendDailyResponse>

    // ✅ 단일 엔드포인트
    @GET("missions") suspend fun getMissions(): BaseResponse<MissionsResponse>
    @POST("missions") suspend fun postMissions(@Body body: MissionsSelectRequest): BaseResponse<MissionsResponse>
}
