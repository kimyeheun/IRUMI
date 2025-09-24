package com.example.irumi.domain.repository

import com.example.irumi.domain.entity.main.*

interface MainRepository {
    suspend fun getUserProfile(): Result<UserProfileEntity>
    suspend fun getDaily(): Result<DailySavingEntity>
    suspend fun getSpending(): Result<SpendingEntity>
    suspend fun getFollows(): Result<List<FollowEntity>>
    suspend fun getBadges(): Result<List<BadgeEntity>>
    suspend fun getStreaks(): Result<List<StreakEntity>>
}
