package com.example.irumi.data.datasource.main

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.main.BadgeListResponse
import com.example.irumi.data.dto.response.main.DailySavingResponse
import com.example.irumi.data.dto.response.main.FollowIdsResponse
import com.example.irumi.data.dto.response.main.FollowListResponse
import com.example.irumi.data.dto.response.main.StreaksResponse
import com.example.irumi.data.dto.response.main.UserProfileResponse
import javax.inject.Inject

class MainLocalDataSource @Inject constructor() : MainDataSource {

    override suspend fun getUserProfile(): BaseResponse<UserProfileResponse> =
        BaseResponse(
            status = 200,
            message = "프로필 조회가 완료되었습니다",
            data = UserProfileResponse(
                userId = 21,
                name = "TESTER",
                budget = 1_000,
                profileImageUrl = "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/profile/default1.jpg"
            )
        )

    override suspend fun getDaily(): BaseResponse<DailySavingResponse> =
        BaseResponse(
            status = 200,
            message = "오늘 절약 점수 조회 성공",
            data = DailySavingResponse(
                savingScore = 100,
                totalSpending = 0
            )
        )

    // spending API는 실제 사용 안 함(daily.totalSpending 사용)
    // override suspend fun getSpending(): BaseResponse<SpendingResponse> = ...

    /** 팔로우 목록 */
    override suspend fun getFollows(): BaseResponse<FollowListResponse> =
        BaseResponse(
            status = 200,
            message = "팔로우 목록 조회 성공",
            data = FollowListResponse(
                follows = listOf(
                    // ⚠ 아래 생성자 파라미터는 실제 DTO 정의에 맞게 수정하세요.
                    // 흔한 케이스: (followUserId, nickname, profileImageUrl, followedAt)
                    FollowListResponse.Follow(
                        followUserId = 23,
                        nickname = "사용자23",
                        profileImageUrl = "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/profile/default2.jpg",
                        followedAt = "2025-09-25T00:56:41.728827"
                    ),
                    FollowListResponse.Follow(
                        followUserId = 22,
                        nickname = "사용자22",
                        profileImageUrl = "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/profile/default3.jpg",
                        followedAt = "2025-09-25T00:56:45.358032"
                    )
                )
            )
        )

    override suspend fun getBadges(): BaseResponse<BadgeListResponse> =
        BaseResponse(
            status = 200,
            message = "뱃지 리스트 조회 성공",
            data = BadgeListResponse(
                badges = listOf(
                    BadgeListResponse.Badge(
                        badgeId = 5,
                        badgeName = "streak5",
                        badgeDescription = "100원",
                        level = 3,
                        badgeImageUrl = "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/badges/streak5.png",
                        createdAt = "2025-09-24T15:59:52"
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
                    StreaksResponse.Streak(
                        date = "2025-09-24",
                        missionsCompleted = 3,
                        spending = 0,
                        isActive = true
                    ),
                    StreaksResponse.Streak(
                        date = "2025-09-23",
                        missionsCompleted = 3,
                        spending = 10000,
                        isActive = true
                    ),
                    StreaksResponse.Streak(
                        date = "2025-09-22",
                        missionsCompleted = 3,
                        spending = 0,
                        isActive = true
                    )
                )
            )
        )

    override suspend fun getFollowIds(): BaseResponse<FollowIdsResponse> =
        BaseResponse(
            status = 200,
            message = "팔로우 정보 조회 성공",
            data = FollowIdsResponse(
                follows = listOf(
                    FollowIdsResponse.FollowEntry(
                        followeeId = 23,
                        followedAt = "2025-09-25T00:56:41.728827"
                    ),
                    FollowIdsResponse.FollowEntry(
                        followeeId = 22,
                        followedAt = "2025-09-25T00:56:45.358032"
                    )
                )
            )
        )

    override suspend fun postFollow(targetUserId: Int): BaseResponse<Unit?> =
        BaseResponse(status = 201, message = "팔로우 성공", data = null)

    override suspend fun deleteFollow(targetUserId: Int): BaseResponse<Unit?> =
        BaseResponse(status = 200, message = "언팔로우 성공", data = null)
}
