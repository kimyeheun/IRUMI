package com.example.irumi.data.datasource.main

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.main.BadgeListResponse
import com.example.irumi.data.dto.response.main.DailySavingResponse
import com.example.irumi.data.dto.response.main.FollowIdsResponse
import com.example.irumi.data.dto.response.main.FollowListResponse
import com.example.irumi.data.dto.response.main.SpendingResponse
import com.example.irumi.data.dto.response.main.StreaksResponse
import com.example.irumi.data.dto.response.main.UserProfileResponse
import com.example.irumi.data.service.MainService
import javax.inject.Inject

class MainRemoteDataSource @Inject constructor(
    private val service: MainService
) : MainDataSource {

    override suspend fun getUserProfile(): BaseResponse<UserProfileResponse> =
        service.getUserProfile()

    override suspend fun getDaily(): BaseResponse<DailySavingResponse> =
        service.getDailySaving()

    override suspend fun getSpending(): BaseResponse<SpendingResponse> {
        TODO("Not yet implemented")
    }

//    override suspend fun getSpending(): BaseResponse<SpendingResponse> =
//        service.getSpending()

    override suspend fun getFollows(): BaseResponse<FollowListResponse> =
        service.getFollows()

    override suspend fun getBadges(): BaseResponse<BadgeListResponse> =
        service.getBadges()

    override suspend fun getStreaks(): BaseResponse<StreaksResponse> =
        service.getStreaks()

    override suspend fun getFollowIds(): BaseResponse<FollowIdsResponse> =
        service.getFollowIds()

    override suspend fun postFollow(targetUserId: Int): BaseResponse<Unit?> =
        service.postFollow(targetUserId)

    override suspend fun deleteFollow(targetUserId: Int): BaseResponse<Unit?> =
        service.deleteFollow(targetUserId)
}
