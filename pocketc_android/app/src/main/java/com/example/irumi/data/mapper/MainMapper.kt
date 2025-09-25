package com.example.irumi.data.mapper

import com.example.irumi.data.dto.response.main.*
import com.example.irumi.domain.entity.main.*
import com.example.irumi.data.dto.response.main.FriendDailyResponse
import com.example.irumi.domain.entity.main.FriendDailyEntity
import com.example.irumi.data.dto.response.main.MissionResponse
import com.example.irumi.data.dto.response.main.MissionsResponse
import com.example.irumi.domain.entity.main.MissionEntity
import com.example.irumi.domain.entity.main.MissionsEntity

fun UserProfileResponse.toEntity() = UserProfileEntity(
    userId = userId,
    name = name,
    budget = budget,
    profileImageUrl = profileImageUrl
)

fun DailySavingResponse.toEntity() = DailySavingEntity(
    savingScore, totalSpending
)

fun SpendingResponse.toEntity() = SpendingEntity(
    totalSpending
)

fun FollowListResponse.toEntity(): List<FollowEntity> =
    follows.map { FollowEntity(it.followUserId, it.nickname, it.profileImageUrl) }

fun BadgeListResponse.toEntity(): List<BadgeEntity> =
    badges.map { BadgeEntity(it.badgeId, it.badgeName, it.badgeDescription, it.level, it.badgeImageUrl, it.createdAt) }

fun StreaksResponse.toEntity(): List<StreakEntity> =
    streaks.map { StreakEntity(it.date, it.missionsCompleted, it.spending, it.isActive) }

fun FollowIdsResponse.toEntity(): List<FollowInfoEntity> =
    follows.map { FollowInfoEntity(it.followUserId, it.followedAt) }

fun FriendDailyResponse.toEntity() = FriendDailyEntity(
    me = me.toEntity(),
    friend = friend.toEntity()
)

fun MissionResponse.toEntity() = MissionEntity(
    missionId = missionId,
    subId = subId,
    type = type,
    mission = mission,
    status = status,
    progress = progress
)

fun MissionsResponse.toEntity() = MissionsEntity(
    missionReceived = missionReceived,
    missions = missions.map { it.toEntity() }
)