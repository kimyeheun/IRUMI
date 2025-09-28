package com.example.irumi.data.datasource.main

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.main.MissionsSelectRequest
import com.example.irumi.data.dto.response.main.BadgeListResponse
import com.example.irumi.data.dto.response.main.FriendStreaksResponse
import com.example.irumi.data.service.MainService
import javax.inject.Inject

class MainRemoteDataSource @Inject constructor(
    private val service: MainService
) : MainDataSource {
    override suspend fun getUserProfile() = service.getUserProfile()
    override suspend fun getDaily() = service.getDailySaving()
    override suspend fun getFollowIds() = service.getFollowIds()
    override suspend fun getBadges() = service.getBadges()
    override suspend fun getStreaks() = service.getStreaks()
    override suspend fun postFollow(targetUserId: Int) = service.postFollow(targetUserId)
    override suspend fun deleteFollow(targetUserId: Int) = service.deleteFollow(targetUserId)
    override suspend fun getDailyWithFriend(friendId: Int) = service.getDailyWithFriend(friendId)

    override suspend fun getStreaksWithFriend(friendId: Int): BaseResponse<FriendStreaksResponse> =
        service.getStreaksWithFriend(friendId)

    override suspend fun getBadgesWithFriend(friendId: Int): BaseResponse<BadgeListResponse> =
        service.getBadgesWithFriend(friendId)

    override suspend fun getMissions() = service.getMissions()
    override suspend fun postMissions(selected: List<Int>) =
        service.postMissions(MissionsSelectRequest(selected))
}
