package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.main.MainDataSource
import com.example.irumi.data.dto.response.main.SpendingResponse
import com.example.irumi.data.mapper.*
import com.example.irumi.domain.entity.main.*
import com.example.irumi.domain.repository.MainRepository
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val dataSource: MainDataSource
) : MainRepository {

    // BaseResponse<T>에서 data가 null이면 예외
    private fun <T> requireData(data: T?, what: String): T =
        data ?: throw IllegalStateException("$what: 서버 응답 data가 비어있습니다.")

    override suspend fun getUserProfile(): Result<UserProfileEntity> = runCatching {
        val res = dataSource.getUserProfile()
        requireData(res.data, "getUserProfile").toEntity()
    }

    override suspend fun getDaily(): Result<DailySavingEntity> = runCatching {
        val res = dataSource.getDaily()
        requireData(res.data, "getDaily").toEntity()
    }

    /**
     * /users/spending 는 403 → daily.totalSpending으로 래핑해서 돌려준다
     */
    override suspend fun getSpending(): Result<SpendingEntity> = runCatching {
        val daily = requireData(dataSource.getDaily().data, "getDaily(for spending)")
        SpendingResponse(daily.totalSpending).toEntity()
    }

    /**
     * 서버 스키마: { follows: [{followeeId, followedAt}] }
     * 닉네임/이미지는 없음 → UI에선 id 기반으로 표시(예: "친구 23") 또는 별도 프로필 조회 필요
     */
    override suspend fun getFollows(): Result<List<FollowEntity>> =
        Result.failure(UnsupportedOperationException("현재 /users/follows는 닉네임/이미지가 없어 FollowEntity로 매핑할 수 없습니다. getFollowIds()를 사용하세요."))

    override suspend fun getBadges(): Result<List<BadgeEntity>> = runCatching {
        val res = dataSource.getBadges()
        requireData(res.data, "getBadges").toEntity()
    }

    override suspend fun getStreaks(): Result<List<StreakEntity>> = runCatching {
        val res = dataSource.getStreaks()
        requireData(res.data, "getStreaks").toEntity()
    }

    // 실제 엔드포인트에 맞춘 팔로우 아이디/시각 목록
    override suspend fun getFollowIds(): Result<List<FollowInfoEntity>> = runCatching {
        val res = dataSource.getFollowIds()
        requireData(res.data, "getFollowIds").toEntity()
    }

    override suspend fun follow(targetUserId: Int): Result<Unit> = runCatching {
        dataSource.postFollow(targetUserId) // data는 사용 안 함
        Unit
    }

    override suspend fun unfollow(targetUserId: Int): Result<Unit> = runCatching {
        dataSource.deleteFollow(targetUserId)
        Unit
    }
}
