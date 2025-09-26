package com.example.irumi.data.mapper

import com.example.irumi.data.dto.response.main.BadgeListResponse
import com.example.irumi.data.dto.response.main.DailySavingResponse
import com.example.irumi.data.dto.response.main.FollowIdsResponse
import com.example.irumi.data.dto.response.main.FollowItem
import com.example.irumi.data.dto.response.main.FollowListResponse
import com.example.irumi.data.dto.response.main.FriendDailyResponse
import com.example.irumi.data.dto.response.main.MissionResponse
import com.example.irumi.data.dto.response.main.MissionsResponse
import com.example.irumi.data.dto.response.main.SpendingResponse
import com.example.irumi.data.dto.response.main.StreaksResponse
import com.example.irumi.data.dto.response.main.UserProfileResponse
import com.example.irumi.domain.entity.main.BadgeEntity
import com.example.irumi.domain.entity.main.DailySavingEntity
import com.example.irumi.domain.entity.main.FollowEntity
import com.example.irumi.domain.entity.main.FollowInfoEntity
import com.example.irumi.domain.entity.main.FriendDailyEntity
import com.example.irumi.domain.entity.main.MissionEntity
import com.example.irumi.domain.entity.main.MissionsEntity
import com.example.irumi.domain.entity.main.SpendingEntity
import com.example.irumi.domain.entity.main.StreakEntity
import com.example.irumi.domain.entity.main.UserProfileEntity

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
    follows.map { FollowInfoEntity(it.followUserId, it.followedAt, it.followeeName, it.followeeProfile) }

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

fun FollowItem.toFollowInfoEntity() = FollowInfoEntity(
    followUserId = followUserId,
    followedAt = followedAt,
    followeeName = followeeName,
    followeeProfile = followeeProfile
)