package com.example.irumi.domain.repository

import com.example.irumi.domain.entity.main.*

interface MainRepository {
    suspend fun getUserProfile(): Result<UserProfileEntity>
    suspend fun getDaily(): Result<DailySavingEntity>
    suspend fun getSpending(): Result<SpendingEntity>
    suspend fun getFollows(): Result<List<FollowEntity>>
    suspend fun getBadges(): Result<List<BadgeEntity>>
    suspend fun getStreaks(): Result<List<StreakEntity>>

    suspend fun getFollowIds(): Result<List<FollowInfoEntity>>
    suspend fun follow(targetUserId: Int): Result<Unit>
    suspend fun unfollow(targetUserId: Int): Result<Unit>
    suspend fun getDailyWithFriend(friendId: Int): Result<FriendDailyEntity>

    // 미션(일/주/월) – userId 필요
    suspend fun getDailyMissions(userId: Int): Result<MissionsEntity>
    suspend fun getWeeklyMissions(userId: Int): Result<MissionsEntity>
    suspend fun getMonthlyMissions(userId: Int): Result<MissionsEntity>

    // 선택 제출
    suspend fun submitMissions(selected: List<Int>): Result<MissionsEntity>
}
