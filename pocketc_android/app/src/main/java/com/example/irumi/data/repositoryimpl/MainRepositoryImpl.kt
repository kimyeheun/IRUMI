package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.main.MainDataSource
import com.example.irumi.data.dto.response.main.SpendingResponse
import com.example.irumi.data.mapper.toEntity
import com.example.irumi.domain.entity.main.BadgeEntity
import com.example.irumi.domain.entity.main.DailySavingEntity
import com.example.irumi.domain.entity.main.FollowEntity
import com.example.irumi.domain.entity.main.FollowInfoEntity
import com.example.irumi.domain.entity.main.FriendDailyEntity
import com.example.irumi.domain.entity.main.MissionsEntity
import com.example.irumi.domain.entity.main.SpendingEntity
import com.example.irumi.domain.entity.main.StreakEntity
import com.example.irumi.domain.entity.main.UserProfileEntity
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

    override suspend fun getSpending(): Result<SpendingEntity> = runCatching {
        val daily = requireData(dataSource.getDaily().data, "getDaily(for spending)")
        SpendingResponse(daily.totalSpending).toEntity()
    }

    override suspend fun getFollows(): Result<List<FollowEntity>> =
        Result.failure(
            UnsupportedOperationException(
                "현재 /users/follows는 닉네임/이미지가 없어 FollowEntity로 매핑할 수 없습니다. getFollowIds()를 사용하세요."
            )
        )

    override suspend fun getBadges(): Result<List<BadgeEntity>> = runCatching {
        val res = dataSource.getBadges()
        requireData(res.data, "getBadges").toEntity()
    }

    override suspend fun getStreaks(): Result<List<StreakEntity>> = runCatching {
        val res = dataSource.getStreaks()
        requireData(res.data, "getStreaks").toEntity()
    }

    override suspend fun getFollowIds(): Result<List<FollowInfoEntity>> = runCatching {
        dataSource.getFollowIds().data!!.toEntity()
    }

    override suspend fun follow(targetUserId: Int): Result<Unit> = runCatching {
        dataSource.postFollow(targetUserId)
        Unit
    }

    override suspend fun follow(userCode: String): Result<Unit> = runCatching {
        dataSource.postFollow(userCode)
        Unit
    }

    override suspend fun unfollow(targetUserId: Int): Result<Unit> = runCatching {
        dataSource.deleteFollow(targetUserId)
        Unit
    }

    override suspend fun getStreaksWithFriend(friendId: Int): Result<Pair<String, List<StreakEntity>>> = runCatching {
        val res = dataSource.getStreaksWithFriend(friendId)
        requireData(res.data, "getStreaksWithFriend").toEntity()
    }

    override suspend fun getBadgesWithFriend(friendId: Int): Result<List<BadgeEntity>> = runCatching {
        val res = dataSource.getBadgesWithFriend(friendId)
        requireData(res.data, "getBadgesWithFriend").toEntity()
    }

    override suspend fun getDailyWithFriend(friendId: Int): Result<FriendDailyEntity> = runCatching {
        val res = dataSource.getDailyWithFriend(friendId)
        requireData(res.data, "getDailyWithFriend").toEntity()
    }

    override suspend fun getMissions(): Result<MissionsEntity> = runCatching {
        val res = dataSource.getMissions()
        requireData(res.data, "getMissions").toEntity()
    }

    override suspend fun submitMissions(selected: List<Int>): Result<MissionsEntity> = runCatching {
        val res = dataSource.postMissions(selected)
        requireData(res.data, "postMissions").toEntity()
    }

    override suspend fun getUserCode(): Result<String> =
        runCatching { dataSource.getUserCode().data!!.userCode }
}
