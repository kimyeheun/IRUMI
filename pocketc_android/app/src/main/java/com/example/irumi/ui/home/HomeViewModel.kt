package com.example.irumi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.irumi.domain.repository.MainRepository
import com.example.irumi.domain.entity.main.*

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    /** 전체 새로고침 (홈 최초 진입/풀투리프레시) */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { loadAll() }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "알 수 없는 오류")
                }
        }
    }

    /** 개별 섹션만 갱신하고 싶을 때 호출용 */
    fun reloadMyScore() = launchAndSet {
        val daily = mainRepository.getDaily().getOrThrow()
        _uiState.value = _uiState.value.copy(myScore = daily)
    }

    fun reloadStreaks() = launchAndSet {
        val streaks = mainRepository.getStreaks().getOrThrow()
        _uiState.value = _uiState.value.copy(streaks = streaks)
    }

    fun reloadFollows() = launchAndSet {
        val follows = mainRepository.getFollows().getOrThrow()
        _uiState.value = _uiState.value.copy(follows = follows)
    }

    // ---------- 내부 ----------

    private suspend fun loadAll() = coroutineScope {
        val profileDef   = async { mainRepository.getUserProfile() }
        val dailyDef     = async { mainRepository.getDaily() }
        val spendingDef  = async { mainRepository.getSpending() }
        val followsDef   = async { mainRepository.getFollows() }
        val badgesDef    = async { mainRepository.getBadges() }
        val streaksDef   = async { mainRepository.getStreaks() }

        // 전부 대기
        awaitAll(profileDef, dailyDef, spendingDef, followsDef, badgesDef, streaksDef)

        val profile   = profileDef.await().getOrThrow()
        val daily     = dailyDef.await().getOrThrow()
        val spending  = spendingDef.await().getOrThrow()
        val follows   = followsDef.await().getOrThrow()
        val badges    = badgesDef.await().getOrThrow()
        val streaks   = streaksDef.await().getOrThrow()

        _uiState.value = HomeUiState(
            isLoading = false,
            error = null,
            profile = profile,
            myScore = daily,
            todaySpending = spending,
            follows = follows,
            badges = badges,
            streaks = streaks
        )
    }

    private fun launchAndSet(block: suspend () -> Unit) = viewModelScope.launch {
        runCatching { block() }.onFailure { e ->
            _uiState.value = _uiState.value.copy(error = e.message ?: "알 수 없는 오류")
        }
    }
}

/** Home 화면에서 필요한 묶음 상태 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val profile: UserProfileEntity? = null,          // /users/me
    val myScore: DailySavingEntity? = null,           // /users/daily
    val todaySpending: SpendingEntity? = null,        // /users/spending
    val follows: List<FollowEntity> = emptyList(),    // /users/follows
    val badges: List<BadgeEntity> = emptyList(),      // /users/badges
    val streaks: List<StreakEntity> = emptyList()     // /users/streaks
)
