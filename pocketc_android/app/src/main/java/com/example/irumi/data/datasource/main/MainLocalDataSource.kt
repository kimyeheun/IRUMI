package com.example.irumi.data.datasource.main

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.main.BadgeListResponse
import com.example.irumi.data.dto.response.main.BadgeListResponse.BadgeDto
import com.example.irumi.data.dto.response.main.DailySavingResponse
import com.example.irumi.data.dto.response.main.FollowListResponse
import com.example.irumi.data.dto.response.main.FollowListResponse.FollowDto
import com.example.irumi.data.dto.response.main.SpendingResponse
import com.example.irumi.data.dto.response.main.StreaksResponse
import com.example.irumi.data.dto.response.main.StreaksResponse.StreakDto
import com.example.irumi.data.dto.response.main.UserProfileResponse
import javax.inject.Inject

class MainLocalDataSource @Inject constructor() : MainDataSource {

    override suspend fun getUserProfile(): BaseResponse<UserProfileResponse> =
        BaseResponse(
            status = 200,
            message = "프로필 조회가 완료되었습니다",
            data = UserProfileResponse(
                userId = 123,
                name = "김철수",
                budget = "1000000",
                profileImageUrl = "https://bucket.s3.amazonaws.com/profile/123/profile.jpg?v=1737459200000"
            )
        )

    override suspend fun getDaily(): BaseResponse<DailySavingResponse> =
        BaseResponse(
            status = 200,
            message = "오늘 절약 점수 조회 성공",
            data = DailySavingResponse(
                savingScore = 87,
                totalSpending = 45200
            )
        )

    override suspend fun getSpending(): BaseResponse<SpendingResponse> =
        BaseResponse(
            status = 200,
            message = "오늘 누적 지출 조회 성공",
            data = SpendingResponse(
                totalSpending = 45200
            )
        )

    override suspend fun getFollows(): BaseResponse<FollowListResponse> =
        BaseResponse(
            status = 200,
            message = "팔로우 목록 조회 성공",
            data = FollowListResponse(
                follows = listOf(
                    FollowDto(101, "절약왕", "https://cdn.example.com/users/101.png"),
                    FollowDto(102, "소비조절러", "https://cdn.example.com/users/102.png")
                )
            )
        )

    override suspend fun getBadges(): BaseResponse<BadgeListResponse> =
        BaseResponse(
            status = 200,
            message = "뱃지 리스트 조회 성공",
            data = BadgeListResponse(
                badges = listOf(
                    BadgeDto(
                        badgeId = 1,
                        badgeName = "첫 절약 성공",
                        badgeDescription = "첫 번째 절약 미션을 완료했습니다",
                        level = 3,
                        badgeImageUrl = "https://cdn.example.com/badges/badge1.png",
                        createdAt = "2025-09-01T10:15:00Z"
                    ),
                    BadgeDto(
                        badgeId = 2,
                        badgeName = "한 달 연속 절약",
                        badgeDescription = "30일 동안 절약에 성공했습니다",
                        level = 5,
                        badgeImageUrl = "https://cdn.example.com/badges/badge2.png",
                        createdAt = "2025-09-10T09:00:00Z"
                    )
                )
            )
        )

    override suspend fun getStreaks(): BaseResponse<StreaksResponse> =
        BaseResponse(
            status = 200,
            message = "스트릭 조회 성공",
            data = StreaksResponse(
                streaks = listOf(
                    StreakDto("2025-09-09", missionsCompleted = 3, spending = 12500, isActive = true),
                    StreakDto("2025-09-10", missionsCompleted = 2, spending = 8000,  isActive = false),
                    StreakDto("2025-09-11", missionsCompleted = 1, spending = 5000,  isActive = true)
                )
            )
        )
}
