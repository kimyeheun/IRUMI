package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.main.MainDataSource
import com.example.irumi.data.mapper.*
import com.example.irumi.domain.entity.main.*
import com.example.irumi.domain.repository.MainRepository
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val dataSource: MainDataSource
) : MainRepository {

    override suspend fun getUserProfile(): Result<UserProfileEntity> =
        runCatching { dataSource.getUserProfile().data!!.toEntity() }

    override suspend fun getDaily(): Result<DailySavingEntity> =
        runCatching { dataSource.getDaily().data!!.toEntity() }

    override suspend fun getSpending(): Result<SpendingEntity> =
        runCatching { dataSource.getSpending().data!!.toEntity() }

    override suspend fun getFollows(): Result<List<FollowEntity>> =
        runCatching { dataSource.getFollows().data!!.toEntity() }

    override suspend fun getBadges(): Result<List<BadgeEntity>> =
        runCatching { dataSource.getBadges().data!!.toEntity() }

    override suspend fun getStreaks(): Result<List<StreakEntity>> =
        runCatching { dataSource.getStreaks().data!!.toEntity() }
}
