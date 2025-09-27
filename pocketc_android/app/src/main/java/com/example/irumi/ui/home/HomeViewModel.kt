package com.example.irumi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.domain.entity.main.BadgeEntity
import com.example.irumi.domain.entity.main.DailySavingEntity
import com.example.irumi.domain.entity.main.FollowInfoEntity
import com.example.irumi.domain.entity.main.FriendDailyEntity
import com.example.irumi.domain.entity.main.MissionEntity
import com.example.irumi.domain.entity.main.SpendingEntity
import com.example.irumi.domain.entity.main.StreakEntity
import com.example.irumi.domain.entity.main.UserProfileEntity
import com.example.irumi.domain.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

//    init {
//        Timber.d("[HomeVM] init -> refresh()")
//        refresh()
//    }

    /** 전체 새로고침 (홈 최초 진입/풀투리프레시) */
    fun refresh() {
        viewModelScope.launch {
            Timber.d("[HomeVM] refresh() start")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { loadAll() }
                .onSuccess {
                    Timber.d("[HomeVM] refresh() success -> uiState=%s", _uiState.value.summary())
                }
                .onFailure { e ->
                    Timber.e(e, "[HomeVM] refresh() failure")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "알 수 없는 오류"
                    )
                }
        }
    }

    /** 개별 섹션 갱신: 데일리 점수/지출 */
    fun reloadMyScore() = launchAndSet("[reloadMyScore]") {
        val daily = mainRepository.getDaily().getOrThrow().also {
            Timber.d("[HomeVM] reloadMyScore(): savingScore=%d, totalSpending=%d", it.savingScore, it.totalSpending)
        }
        _uiState.value = _uiState.value.copy(
            myScore = daily,
            todaySpending = SpendingEntity(daily.totalSpending)
        )
        Timber.d("[HomeVM] reloadMyScore() -> uiState=%s", _uiState.value.summary())
    }

    /** 개별 섹션 갱신: 스트릭 */
    fun reloadStreaks() = launchAndSet("[reloadStreaks]") {
        val streaks = mainRepository.getStreaks().getOrThrow()
        Timber.d("[HomeVM] reloadStreaks(): size=%d, first=%s", streaks.size, streaks.firstOrNull()?.date ?: "null")
        _uiState.value = _uiState.value.copy(streaks = streaks)
    }

    /** 개별 섹션 갱신: 팔로우(IDs) */
    fun reloadFollowIds() = launchAndSet("[reloadFollowIds]") {
        mainRepository.getFollowIds()
            .onSuccess {
                Timber.d("[HomeVM] reloadFollowIds(): size=%d, ids=%s", it.size, it.joinToString { f -> f.followUserId.toString() })
                _uiState.value = _uiState.value.copy(followInfos = it)
            }
            .onFailure { Timber.d("[HomeVM] reloadFollowIds() ERROR") }
    }

    /** 개별 섹션 갱신: 뱃지 */
    fun reloadBadges() = launchAndSet("[reloadBadges]") {
        val badges = mainRepository.getBadges().getOrThrow()
        Timber.d("[HomeVM] reloadBadges(): size=%d", badges.size)
        _uiState.value = _uiState.value.copy(badges = badges)
    }

    /** 개별 섹션 갱신: 미션 (단일 엔드포인트) */
    fun reloadMissions() = launchAndSet("[reloadMissions]") {
        val missions = mainRepository.getMissions().getOrThrow()
        Timber.d("[HomeVM] reloadMissions(): received=%s, size=%d", missions.missionReceived, missions.missions.size)
        _uiState.value = _uiState.value.copy(
            missionReceived = missions.missionReceived,
            missions = missions.missions
        )
    }

    /** 선택한 미션 제출 -> 응답으로 갱신 */
    fun submitMissions(selectedIds: List<Int>) = launchAndSet("[submitMissions]") {
        Timber.d("[HomeVM] submitMissions() sending: %s", selectedIds.joinToString())
        val result = mainRepository.submitMissions(selectedIds).getOrThrow()
        Timber.d("[HomeVM] submitMissions(): received=%s, size=%d", result.missionReceived, result.missions.size)
        _uiState.value = _uiState.value.copy(
            missionReceived = result.missionReceived,
            missions = result.missions
        )
    }

    /** 친구 비교 데이터 로드(캐시) */
    fun reloadFriendDaily(friendId: Int, skipIfCached: Boolean = true) =
        launchAndSet("[reloadFriendDaily:$friendId]") {
            if (friendId == 0) return@launchAndSet
            if (skipIfCached && _uiState.value.friendDaily.containsKey(friendId)) {
                Timber.d("[HomeVM] reloadFriendDaily(%d) skipped (cached)", friendId)
                return@launchAndSet
            }
            val pair = mainRepository.getDailyWithFriend(friendId).getOrThrow()
            _uiState.value = _uiState.value.copy(
                friendDaily = _uiState.value.friendDaily + (friendId to pair)
            )
            Timber.d(
                "[HomeVM] reloadFriendDaily(%d): me=%d/%d, friend=%d/%d",
                friendId, pair.me.savingScore, pair.me.totalSpending,
                pair.friend.savingScore, pair.friend.totalSpending
            )
        }

    // ---------- 내부 ----------

    /** 병렬 로딩 */
    private suspend fun loadAll() = coroutineScope {
        Timber.d("[HomeVM] loadAll() start")

        val profileDef = async {
            runCatching { mainRepository.getUserProfile().getOrThrow() }
                .also { r ->
                    r.onSuccess { Timber.d("[HomeVM] /me OK: id=%d, name=%s, budget=%d", it.userId, it.name, it.budget) }
                        .onFailure { Timber.e(it, "[HomeVM] /me ERROR") }
                }
        }

        val dailyDef = async {
            runCatching { mainRepository.getDaily().getOrThrow() }
                .also { r ->
                    r.onSuccess { Timber.d("[HomeVM] /daily OK: savingScore=%d, totalSpending=%d", it.savingScore, it.totalSpending) }
                        .onFailure { Timber.e(it, "[HomeVM] /daily ERROR") }
                }
        }

        val followIdsDef = async {
            runCatching { mainRepository.getFollowIds().getOrThrow() }
                .also { r ->
                    r.onSuccess { Timber.d("[HomeVM] /follows(ids) OK: size=%d, ids=%s", it.size, it.joinToString { f -> f.followUserId.toString() }) }
                        .onFailure { Timber.e(it, "[HomeVM] /follows(ids) ERROR") }
                }
        }

        val badgesDef = async {
            runCatching { mainRepository.getBadges().getOrThrow() }
                .also { r ->
                    r.onSuccess { Timber.d("[HomeVM] /badges OK: size=%d", it.size) }
                        .onFailure { Timber.e(it, "[HomeVM] /badges ERROR") }
                }
        }

        val streaksDef = async {
            runCatching { mainRepository.getStreaks().getOrThrow() }
                .also { r ->
                    r.onSuccess { Timber.d("[HomeVM] /streaks OK: size=%d", it.size) }
                        .onFailure { Timber.e(it, "[HomeVM] /streaks ERROR") }
                }
        }

        val missionsDef = async {
            runCatching { mainRepository.getMissions().getOrThrow() }
                .also { r ->
                    r.onSuccess {
                        Timber.d("[HomeVM] /missions OK: received=%s, size=%d",
                            it.missionReceived, it.missions.size)
                    }
                        .onFailure { Timber.e(it, "[HomeVM] /missions ERROR") }
                }
        }

        awaitAll(profileDef, dailyDef, followIdsDef, badgesDef, streaksDef, missionsDef)

        val current = _uiState.value
        val profile = profileDef.await().getOrNull() ?: current.profile
        val daily = dailyDef.await().getOrNull() ?: current.myScore
        val followInfos = followIdsDef.await().getOrNull() ?: current.followInfos
        val badges = badgesDef.await().getOrNull() ?: current.badges
        val streaks = streaksDef.await().getOrNull() ?: current.streaks
        val missions = missionsDef.await().getOrNull()

        _uiState.value = current.copy(
            isLoading = false,
            error = null,
            profile = profile,
            myScore = daily,
            todaySpending = daily?.let { SpendingEntity(it.totalSpending) } ?: current.todaySpending,
            followInfos = followInfos,
            badges = badges,
            streaks = streaks,
            missionReceived = missions?.missionReceived ?: current.missionReceived,
            missions = missions?.missions ?: current.missions
        )

        Timber.d("[HomeVM] loadAll() done -> uiState=%s", _uiState.value.summary())
    }

    private fun launchAndSet(tag: String, block: suspend () -> Unit) = viewModelScope.launch {
        Timber.d("[HomeVM] %s start", tag)
        runCatching { block() }
            .onSuccess { Timber.d("[HomeVM] %s success -> uiState=%s", tag, _uiState.value.summary()) }
            .onFailure { e ->
                Timber.e(e, "[HomeVM] %s failure", tag)
                _uiState.value = _uiState.value.copy(error = e.message ?: "알 수 없는 오류")
            }
    }

    /** 팔로우 액션 후 리스트 갱신은 FollowIds 사용 */
    fun follow(targetUserId: Int) = viewModelScope.launch {
        Timber.d("[HomeVM] follow(%d) start", targetUserId)
        mainRepository.follow(targetUserId)
            .onSuccess {
                Timber.d("[HomeVM] follow(%d) success -> reloadFollowIds()", targetUserId)
                reloadFollowIds()
            }
            .onFailure { e ->
                Timber.e(e, "[HomeVM] follow(%d) failure", targetUserId)
                _uiState.value = _uiState.value.copy(error = e.message ?: "팔로우 실패")
            }
    }

    fun unfollow(targetUserId: Int) = viewModelScope.launch {
        Timber.d("[HomeVM] unfollow(%d) start", targetUserId)
        mainRepository.unfollow(targetUserId)
            .onSuccess {
                Timber.d("[HomeVM] unfollow(%d) success -> reloadFollowIds()", targetUserId)
                reloadFollowIds()
                _uiState.value = _uiState.value.copy(
                    friendDaily = _uiState.value.friendDaily - targetUserId
                )
            }
            .onFailure { e ->
                Timber.e(e, "[HomeVM] unfollow(%d) failure", targetUserId)
                _uiState.value = _uiState.value.copy(error = e.message ?: "언팔로우 실패")
            }
    }
}

/** Home 화면 상태 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val profile: UserProfileEntity? = null,                 // /users/me
    val myScore: DailySavingEntity? = null,                 // /users/daily
    val todaySpending: SpendingEntity? = null,

    val followInfos: List<FollowInfoEntity> = emptyList(),  // /users/follows
    val badges: List<BadgeEntity> = emptyList(),            // /users/badges
    val streaks: List<StreakEntity> = emptyList(),          // /users/streaks

    // 미션 (단일 엔드포인트)
    val missionReceived: Boolean? = null,
    val missions: List<MissionEntity> = emptyList(),

    // 친구 비교 캐시: friendId → (me, friend)
    val friendDaily: Map<Int, FriendDailyEntity> = emptyMap()
)

/** 디버깅용 요약 문자열 */
private fun HomeUiState.summary(): String =
    "loading=$isLoading, " +
            "err=${error != null}, " +
            "me=${profile?.userId ?: "null"}, " +
            "score=${myScore?.savingScore ?: "null"}, " +
            "spend=${todaySpending?.totalSpending ?: "null"}, " +
            "followIds=${followInfos.size}, " +
            "badges=${badges.size}, " +
            "streaks=${streaks.size}, " +
            "missions(received=$missionReceived)=${missions.size}, " +
            "friendDaily=${friendDaily.size}"
