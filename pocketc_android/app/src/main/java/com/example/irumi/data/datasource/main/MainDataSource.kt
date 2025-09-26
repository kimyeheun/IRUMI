package com.example.irumi.data.datasource.main

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.main.*

interface MainDataSource {
    suspend fun getUserProfile(): BaseResponse<UserProfileResponse>
    suspend fun getDaily(): BaseResponse<DailySavingResponse>
    suspend fun getBadges(): BaseResponse<BadgeListResponse>
    suspend fun getStreaks(): BaseResponse<StreaksResponse>

    suspend fun getFollowIds(): BaseResponse<FollowIdsResponse>
    suspend fun postFollow(targetUserId: Int): BaseResponse<Unit?>
    suspend fun deleteFollow(targetUserId: Int): BaseResponse<Unit?>
    suspend fun getDailyWithFriend(friendId: Int): BaseResponse<FriendDailyResponse>

    // ✅ 미션: userId 필요
    suspend fun getDailyMissions(userId: Int): BaseResponse<MissionsResponse>
    suspend fun getWeeklyMissions(userId: Int): BaseResponse<MissionsResponse>
    suspend fun getMonthlyMissions(userId: Int): BaseResponse<MissionsResponse>

    suspend fun postMissions(selected: List<Int>): BaseResponse<MissionsResponse>
}
