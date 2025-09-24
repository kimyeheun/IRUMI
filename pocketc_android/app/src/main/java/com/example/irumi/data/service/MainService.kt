package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.main.*
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MainService {
    @GET("me")
    suspend fun getUserProfile(): BaseResponse<UserProfileResponse>

    @GET("daily")
    suspend fun getDailySaving(): BaseResponse<DailySavingResponse>

    @GET("spending")
    suspend fun getSpending(): BaseResponse<SpendingResponse>

    @GET("follows")
    suspend fun getFollows(): BaseResponse<FollowListResponse>

    @GET("badges")
    suspend fun getBadges(): BaseResponse<BadgeListResponse>

    @GET("streaks")
    suspend fun getStreaks(): BaseResponse<StreaksResponse>

    /** 팔로우 아이디/시각 목록 */
    @GET("follows")
    suspend fun getFollowIds(): BaseResponse<FollowIdsResponse>

    /** 팔로우 */
    @POST("follows/{targetUserId}")
    suspend fun postFollow(
        @Path("targetUserId") targetUserId: Int
    ): BaseResponse<Unit?>

    /** 언팔로우 */
    @DELETE("follows/{targetUserId}")
    suspend fun deleteFollow(
        @Path("targetUserId") targetUserId: Int
    ): BaseResponse<Unit?>
}
