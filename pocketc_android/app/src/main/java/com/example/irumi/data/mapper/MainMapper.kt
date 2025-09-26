package com.example.irumi.data.mapper

import com.example.irumi.data.dto.response.main.BadgeListResponse
import com.example.irumi.data.dto.response.main.DailySavingResponse
import com.example.irumi.data.dto.response.main.FollowIdsResponse
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

/** 유저 프로필 매핑 */
fun UserProfileResponse.toEntity() = UserProfileEntity(
    userId = userId,
    name = name,
    budget = budget,
    profileImageUrl = profileImageUrl
)

/** 오늘의 절약 점수 매핑 */
fun DailySavingResponse.toEntity() = DailySavingEntity(
    savingScore = savingScore,
    totalSpending = totalSpending
)

/** 지출 매핑 */
fun SpendingResponse.toEntity() = SpendingEntity(
    totalSpending = totalSpending
)

/** 팔로우 리스트 매핑 */
fun FollowListResponse.toEntity(): List<FollowEntity> =
    follows.map { dto ->
        FollowEntity(
            followUserId = dto.followUserId,
            nickname = dto.nickname,
            profileImageUrl = dto.profileImageUrl
        )
    }

/** 뱃지 리스트 매핑 */
fun BadgeListResponse.toEntity(): List<BadgeEntity> =
    badges.map { dto ->
        BadgeEntity(
            badgeId = dto.badgeId,
            badgeName = dto.badgeName,
            badgeDescription = dto.badgeDescription,
            level = dto.level,
            badgeImageUrl = dto.badgeImageUrl,
            createdAt = dto.createdAt
        )
    }

/** 스트릭 매핑 */
fun StreaksResponse.toEntity(): List<StreakEntity> =
    streaks.map { dto ->
        StreakEntity(
            date = dto.date,
            missionsCompleted = dto.missionsCompleted,
            spending = dto.spending,
            isActive = dto.isActive
        )
    }

/** 팔로우 ID/시각 매핑 */
fun FollowIdsResponse.toEntity(): List<FollowInfoEntity> =
    follows.map { dto ->
        FollowInfoEntity(
            followUserId = dto.followUserId,
            followedAt = dto.followedAt,
            followeeName = dto.followeeName,
            followeeProfile = dto.followeeProfile
        )
    }

/** 나와 친구의 절약 정보 매핑 */
fun FriendDailyResponse.toEntity() = FriendDailyEntity(
    me = me.toEntity(),
    friend = friend.toEntity()
)

/** 개별 미션 매핑 */
fun MissionResponse.toEntity() = MissionEntity(
    missionId = missionId,
    subId = subId,
    type = type,
    mission = mission,
    status = status,
    progress = progress,
    value = value,
    template = template
)

fun MissionsResponse.toEntity() = MissionsEntity(
    missionReceived = missionReceived,
    missions = missions.map { it.toEntity() }
)
