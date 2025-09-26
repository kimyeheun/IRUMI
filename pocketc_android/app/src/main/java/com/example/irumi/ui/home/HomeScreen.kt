// ui/home/HomeScreen.kt
package com.example.irumi.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irumi.domain.entity.main.StreakEntity
import com.example.irumi.ui.home.component.*
import com.example.irumi.ui.theme.BrandGreen
import kotlin.math.min
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.min

data class Friend(
    val id: Int,
    val name: String,
    val avatarUrl: String? = null
)

@Composable
fun HomeScreen(
    brand: Color = BrandGreen,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 화면 재진입 시 새로고침
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // "나" + 팔로우 ID로 친구 리스트 구성(닉네임 미확정 → placeholder)
    val friends = remember(state.followInfos, state.profile?.profileImageUrl) {
        listOf(Friend(0, "나", state.profile?.profileImageUrl)) +
                state.followInfos.map { info ->
                    Friend(id = info.followUserId, name = "친구 ${info.followUserId}", avatarUrl = null)
                }
    }

    // 선택된 친구
    var selectedFriend by remember { mutableStateOf(Friend(0, "나")) }
    LaunchedEffect(friends) {
        if (friends.none { it.id == selectedFriend.id }) {
            selectedFriend = friends.firstOrNull() ?: Friend(0, "나")
        }
    }

    // 선택된 친구가 바뀌면 비교 데이터 로드(캐시 사용)
    LaunchedEffect(selectedFriend.id) {
        if (selectedFriend.id != 0) viewModel.reloadFriendDaily(selectedFriend.id)
    }

    // ===== 미션 탭(일/주/월) =====
    var selectedTab by rememberSaveable { mutableStateOf(MissionPeriod.DAILY) }
    // VM 값과 동기화
    LaunchedEffect(state.missionPeriod) {
        if (selectedTab != state.missionPeriod) selectedTab = state.missionPeriod
    }
    // 탭 변경 시 해당 기간 미션 로드
    LaunchedEffect(selectedTab, state.profile?.userId) {
        viewModel.reloadMissions(selectedTab)
    }

    // ===== 오늘 첫 실행: 추천 미션 시트 자동 노출(데일리 탭 한정, 하루 1회) =====
    val dayKey = remember { LocalDate.now(ZoneId.of("Asia/Seoul")).toString() }
    var showMissionSheet by rememberSaveable { mutableStateOf(false) }
    var lastTriggeredByDayKey by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.missions, dayKey, selectedTab) {
        val isDaily = selectedTab == MissionPeriod.DAILY
        if (isDaily && state.missions.isNotEmpty() && lastTriggeredByDayKey != dayKey) {
            showMissionSheet = true
            lastTriggeredByDayKey = dayKey
        }
    }

    // ===== 친구 추가/삭제 UI 상태 =====
    var showAddSheet by remember { mutableStateOf(false) }
    var followLoading by remember { mutableStateOf(false) }
    var followError by remember { mutableStateOf<String?>(null) }
    var pendingFollowTargetId by remember { mutableStateOf<Int?>(null) }

    var pendingUnfollow by remember { mutableStateOf<Friend?>(null) }
    var unfollowLoading by remember { mutableStateOf(false) }
    var unfollowError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // --- 친구 리스트 ---
        FriendList(
            friends = friends,
            selected = selectedFriend,
            brand = brand,
            onSelect = { selectedFriend = it },
            onAddClick = {
                followError = null
                pendingFollowTargetId = null
                showAddSheet = true
            },
            onLongPress = { friend ->
                if (friend.id != 0) {
                    unfollowError = null
                    pendingUnfollow = friend
                }
            },
            getAvatarUrl = { f -> f.avatarUrl }
        )

        Spacer(Modifier.height(16.dp))

        // --- 로딩 & 에러 ---
        if (state.isLoading && state.profile == null) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        // --- 본문: 내 화면 / 친구 화면 ---
        if (selectedFriend.id == 0) {
            MyScoreSection(score = state.myScore?.savingScore ?: 0)
            Spacer(Modifier.height(12.dp))

            // 미션 탭
            MissionTabRow(
                selected = selectedTab,
                onSelect = { selectedTab = it }
            )
            Spacer(Modifier.height(10.dp))

            // 오늘의 미션(새 스키마: missionReceived 제거) — 기존 컴포넌트 시그니처 유지 위해 false 전달
            TodoSection(
                missionReceived = false,
                missions = state.missions
            )
            Spacer(Modifier.height(12.dp))

            BadgesSection(badges = state.badges)
            Spacer(Modifier.height(12.dp))

            StreakSection(
                friendName = null,
                days = state.streaks.toDays(),
                totalDays = 365
            )

            Log.d("HomeScreen", "사고지점2")
        } else {
            // 친구 비교 데이터 (없으면 로딩 중)
            val pair = state.friendDaily[selectedFriend.id]
            if (pair == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
                Spacer(Modifier.height(12.dp))
            } else {
                FriendCompareSection(
                    myScore = pair.me.savingScore,
                    friendScore = pair.friend.savingScore,
                    friendName = selectedFriend.name
                )
                Spacer(Modifier.height(12.dp))
            }

            StreakSection(
                friendName = selectedFriend.name,
                days = state.streaks.toDays(),
                totalDays = 365
            )
        }
        Spacer(Modifier.height(24.dp))
    }

    // ===== 친구 추가 바텀시트 =====
    if (showAddSheet) {
        FriendAddSheet(
            onDismiss = {
                if (!followLoading) {
                    showAddSheet = false
                    followError = null
                    pendingFollowTargetId = null
                }
            },
            isProcessing = followLoading,
            error = followError,
            onFollow = { targetId ->
                followLoading = true
                followError = null
                pendingFollowTargetId = targetId
                viewModel.follow(targetId)
            }
        )

        // followInfos 기준 성공 여부 감지
        LaunchedEffect(state.followInfos, state.error, followLoading, showAddSheet, pendingFollowTargetId) {
            if (!showAddSheet || !followLoading) return@LaunchedEffect
            if (state.error != null) {
                followLoading = false
                followError = state.error
                return@LaunchedEffect
            }
            pendingFollowTargetId?.let { id ->
                val exists = state.followInfos.any { it.followUserId == id }
                if (exists) {
                    followLoading = false
                    followError = null
                    showAddSheet = false
                    pendingFollowTargetId = null
                }
            }
        }
    }

    // ===== 언팔로우 다이얼로그 =====
    pendingUnfollow?.let { friend ->
        AlertDialog(
            onDismissRequest = {
                if (!unfollowLoading) {
                    pendingUnfollow = null
                    unfollowError = null
                }
            },
            title = { Text("팔로우 취소") },
            text = {
                Column {
                    Text("정말 ${friend.name}님을 언팔로우할까요?")
                    unfollowError?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !unfollowLoading,
                    onClick = {
                        unfollowLoading = true
                        unfollowError = null
                        viewModel.unfollow(friend.id)
                    }
                ) {
                    if (unfollowLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("언팔로우")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !unfollowLoading,
                    onClick = {
                        pendingUnfollow = null
                        unfollowError = null
                    }
                ) { Text("취소") }
            }
        )

        // followInfos 기준 언팔 성공 감지
        LaunchedEffect(state.followInfos, state.error, unfollowLoading, pendingUnfollow) {
            if (!unfollowLoading) return@LaunchedEffect
            if (state.error != null) {
                unfollowLoading = false
                unfollowError = state.error
                return@LaunchedEffect
            }
            val removed = state.followInfos.none { it.followUserId == friend.id }
            if (removed) {
                unfollowLoading = false
                unfollowError = null
                pendingUnfollow = null
            }
        }
    }

    // ===== 미션 시트(성공/실패 자동 표기, 기간 토글 가능) =====
    if (showMissionSheet) {
        MissionPickSheet(
            period = state.missionPeriod,
            onChangePeriod = { p -> viewModel.reloadMissions(p) },
            missions = state.missions,
            isProcessing = false,
            onDismiss = { showMissionSheet = false }
        )
    }

    Log.d("HomeScreen", "사고지점3")
}

/** 미션 탭 UI */
@Composable
private fun MissionTabRow(
    selected: MissionPeriod,
    onSelect: (MissionPeriod) -> Unit
) {
    val items = listOf(
        MissionPeriod.DAILY to "일간",
        MissionPeriod.WEEKLY to "주간",
        MissionPeriod.MONTHLY to "월간"
    )
    val selectedIndex = items.indexOfFirst { it.first == selected }.coerceAtLeast(0)
    TabRow(selectedTabIndex = selectedIndex) {
        items.forEachIndexed { index, (period, label) ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onSelect(period) },
                text = { Text(label) }
            )
        }
    }
}

/** StreakEntity -> 고정 길이 Boolean 리스트 */
private fun List<StreakEntity>.toDays(totalDays: Int = 365): List<Boolean> {
    if (isEmpty()) return List(totalDays) { false }
    val sliced = take(min(size, totalDays))
    val flags = sliced.map { it.isActive }
    return if (flags.size >= totalDays) flags.take(totalDays)
    else flags + List(totalDays - flags.size) { false }
}
