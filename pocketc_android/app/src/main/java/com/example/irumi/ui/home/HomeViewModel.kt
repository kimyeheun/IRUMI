package com.example.irumi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.domain.entity.main.*
import com.example.irumi.domain.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Timber.d("[HomeVM] init -> refresh()")
        refresh()
    }

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

    /** 개별 섹션 갱신 */
    fun reloadMyScore() = launchAndSet("[reloadMyScore]") {
        val daily = mainRepository.getDaily().getOrThrow().also {
            Timber.d("[HomeVM] reloadMyScore(): savingScore=%d, totalSpending=%d",
                it.savingScore, it.totalSpending
            )
        }
        _uiState.value = _uiState.value.copy(
            myScore = daily,
            // spending API 제거: daily.totalSpending으로 동기화
            todaySpending = SpendingEntity(daily.totalSpending)
        )
        Timber.d("[HomeVM] reloadMyScore() -> uiState=%s", _uiState.value.summary())
    }

    fun reloadStreaks() = launchAndSet("[reloadStreaks]") {
        val streaks = mainRepository.getStreaks().getOrThrow()
        Timber.d("[HomeVM] reloadStreaks(): size=%d, first=%s",
            streaks.size, streaks.firstOrNull()?.date ?: "null"
        )
        _uiState.value = _uiState.value.copy(streaks = streaks)
    }

    fun reloadFollows() = launchAndSet("[reloadFollows]") {
        val follows = mainRepository.getFollows().getOrThrow()
        Timber.d("[HomeVM] reloadFollows(): size=%d, ids=%s",
            follows.size, follows.joinToString { it.followUserId.toString() }
        )
        _uiState.value = _uiState.value.copy(follows = follows)
    }

    // ---------- 내부 ----------

    private suspend fun loadAll() = coroutineScope {
        Timber.d("[HomeVM] loadAll() start (parallel)")

        val profileDef = async {
            runCatching { mainRepository.getUserProfile().getOrThrow() }
                .onSuccess { Timber.d("[HomeVM] /me OK: id=%d, name=%s, budget=%d",
                    it.userId, it.name, it.budget) }
                .onFailure { Timber.e(it, "[HomeVM] /me ERROR") }
        }

        val dailyDef = async {
            runCatching { mainRepository.getDaily().getOrThrow() }
                .onSuccess { Timber.d("[HomeVM] /daily OK: savingScore=%d, totalSpending=%d",
                    it.savingScore, it.totalSpending) }
                .onFailure { Timber.e(it, "[HomeVM] /daily ERROR") }
        }

        val followsDef = async {
            runCatching { mainRepository.getFollows().getOrThrow() }
                .onSuccess { Timber.d("[HomeVM] /follows OK: size=%d, ids=%s",
                    it.size, it.joinToString { f -> f.followUserId.toString() }) }
                .onFailure { Timber.e(it, "[HomeVM] /follows ERROR") }
        }

        val badgesDef = async {
            runCatching { mainRepository.getBadges().getOrThrow() }
                .onSuccess { Timber.d("[HomeVM] /badges OK: size=%d", it.size) }
                .onFailure { Timber.e(it, "[HomeVM] /badges ERROR") }
        }

        val streaksDef = async {
            runCatching { mainRepository.getStreaks().getOrThrow() }
                .onSuccess { Timber.d("[HomeVM] /streaks OK: size=%d", it.size) }
                .onFailure { Timber.e(it, "[HomeVM] /streaks ERROR") }
        }

        // 모든 네트워크 완료 대기
        awaitAll(profileDef, dailyDef, followsDef, badgesDef, streaksDef)

        val profile = profileDef.await().getOrThrow()
        val daily   = dailyDef.await().getOrThrow()
        val follows = followsDef.await().getOrThrow()
        val badges  = badgesDef.await().getOrThrow()
        val streaks = streaksDef.await().getOrThrow()

        _uiState.value = HomeUiState(
            isLoading = false,
            error = null,
            profile = profile,
            myScore = daily,
            // spending API 대신 daily의 totalSpending 사용
            todaySpending = SpendingEntity(daily.totalSpending),
            follows = follows,
            badges = badges,
            streaks = streaks
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

    fun follow(targetUserId: Int) = viewModelScope.launch {
        Timber.d("[HomeVM] follow(%d) start", targetUserId)
        mainRepository.follow(targetUserId)
            .onSuccess {
                Timber.d("[HomeVM] follow(%d) success -> reloadFollows()", targetUserId)
                reloadFollows()
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
                Timber.d("[HomeVM] unfollow(%d) success -> reloadFollows()", targetUserId)
                reloadFollows()
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

    val profile: UserProfileEntity? = null,          // /users/me
    val myScore: DailySavingEntity? = null,          // /users/daily (savingScore, totalSpending)
    // /users/spending 제거 → daily.totalSpending을 감싸서 제공
    val todaySpending: SpendingEntity? = null,
    val follows: List<FollowEntity> = emptyList(),   // /users/follows
    val badges: List<BadgeEntity> = emptyList(),     // /users/badges
    val streaks: List<StreakEntity> = emptyList()    // /users/streaks
)

/** 디버깅용 요약 문자열 */
private fun HomeUiState.summary(): String =
    "loading=$isLoading, " +
            "err=${error != null}, " +
            "me=${profile?.userId ?: "null"}, " +
            "score=${myScore?.savingScore ?: "null"}, " +
            "spend=${todaySpending?.totalSpending ?: "null"}, " +
            "follows=${follows.size}, " +
            "badges=${badges.size}, " +
            "streaks=${streaks.size}"
