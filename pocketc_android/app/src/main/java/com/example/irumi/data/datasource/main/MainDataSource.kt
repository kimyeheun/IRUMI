package com.example.irumi.data.datasource.main

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.main.BadgeListResponse
import com.example.irumi.data.dto.response.main.DailySavingResponse
import com.example.irumi.data.dto.response.main.StreaksResponse
import com.example.irumi.data.dto.response.main.UserProfileResponse

interface MainDataSource {
    suspend fun getUserProfile(): BaseResponse<UserProfileResponse>
    suspend fun getDaily(): BaseResponse<DailySavingResponse>

    // 제거: suspend fun getSpending(): BaseResponse<SpendingResponse>

    // 서버 스키마가 followeeId만 주므로 닉네임/이미지는 없음
    // suspend fun getFollows(): BaseResponse<FollowListResponse>  // 제거

    // suspend fun getFollowIds(): BaseResponse<FollowIdsResponse>
    suspend fun getBadges(): BaseResponse<BadgeListResponse>
    suspend fun getStreaks(): BaseResponse<StreaksResponse>

    suspend fun postFollow(targetUserId: Int): BaseResponse<Unit?>
    suspend fun deleteFollow(targetUserId: Int): BaseResponse<Unit?>
}