package com.example.irumi.domain.repository

import com.example.irumi.domain.entity.main.BadgeEntity
import com.example.irumi.domain.entity.main.DailySavingEntity
import com.example.irumi.domain.entity.main.FollowEntity
import com.example.irumi.domain.entity.main.FollowInfoEntity
import com.example.irumi.domain.entity.main.FriendDailyEntity
import com.example.irumi.domain.entity.main.MissionsEntity
import com.example.irumi.domain.entity.main.SpendingEntity
import com.example.irumi.domain.entity.main.StreakEntity
import com.example.irumi.domain.entity.main.UserProfileEntity

interface MainRepository {
    suspend fun getUserProfile(): Result<UserProfileEntity>
    suspend fun getDaily(): Result<DailySavingEntity>
    suspend fun getSpending(): Result<SpendingEntity>
    suspend fun getFollows(): Result<List<FollowEntity>>
    suspend fun getBadges(): Result<List<BadgeEntity>>
    suspend fun getStreaks(): Result<List<StreakEntity>>

    suspend fun getFollowIds(): Result<List<FollowInfoEntity>>
    suspend fun follow(targetUserId: Int): Result<Unit>
    suspend fun follow(userCode: String): Result<Unit>
    suspend fun unfollow(targetUserId: Int): Result<Unit>
    suspend fun getDailyWithFriend(friendId: Int): Result<FriendDailyEntity>

    suspend fun getStreaksWithFriend(friendId: Int): Result<Pair<String, List<StreakEntity>>>

    suspend fun getBadgesWithFriend(friendId: Int): Result<List<BadgeEntity>>

    // ✅ 단일 미션
    suspend fun getMissions(): Result<MissionsEntity>
    suspend fun submitMissions(selected: List<Int>): Result<MissionsEntity>
}
