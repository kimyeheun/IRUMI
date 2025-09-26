package com.example.irumi.data.datasource.main

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.main.BadgeListResponse
import com.example.irumi.data.dto.response.main.DailySavingResponse
import com.example.irumi.data.dto.response.main.FollowIdsResponse
import com.example.irumi.data.dto.response.main.FriendDailyResponse
import com.example.irumi.data.dto.response.main.MissionsResponse
import com.example.irumi.data.dto.response.main.StreaksResponse
import com.example.irumi.data.dto.response.main.UserProfileResponse

interface MainDataSource {
    suspend fun getUserProfile(): BaseResponse<UserProfileResponse>
    suspend fun getDaily(): BaseResponse<DailySavingResponse>
    suspend fun getBadges(): BaseResponse<BadgeListResponse>
    suspend fun getStreaks(): BaseResponse<StreaksResponse>

    suspend fun getFollowIds(): BaseResponse<FollowIdsResponse>
    suspend fun postFollow(targetUserId: Int): BaseResponse<Unit?>
    suspend fun deleteFollow(targetUserId: Int): BaseResponse<Unit?>
    suspend fun getDailyWithFriend(friendId: Int): BaseResponse<FriendDailyResponse>

    // 미션(일/주/월) – userId 필요
    suspend fun getDailyMissions(userId: Int): BaseResponse<MissionsResponse>
    suspend fun getWeeklyMissions(userId: Int): BaseResponse<MissionsResponse>
    suspend fun getMonthlyMissions(userId: Int): BaseResponse<MissionsResponse>

    // 추천 미션 선택 제출
    suspend fun postMissions(selected: List<Int>): BaseResponse<MissionsResponse>
}
