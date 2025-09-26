package com.example.irumi.ui.home

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irumi.domain.entity.main.StreakEntity
import com.example.irumi.ui.home.component.*
import com.example.irumi.ui.theme.BrandGreen
import com.example.irumi.ui.theme.LightGray
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.min

data class Friend(
    val id: Int,
    val name: String,
    val avatarUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    brand: Color = BrandGreen,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // ---- 상태바/내비게이션바: 흰 배경 + 어두운 아이콘 설정 ----
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        // ▼ 여기 두 줄을 LightGray로 변경
        window.statusBarColor = LightGray.toArgb()
        window.navigationBarColor = LightGray.toArgb()

        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    // 화면 재진입 시 새로고침
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // "나" + 팔로우 ID로 친구 리스트 구성
    val friends = remember(state.followInfos, state.profile?.profileImageUrl) {
        listOf(Friend(0, "나", state.profile?.profileImageUrl)) +
                state.followInfos.map { info ->
                    Friend(
                        id = info.followUserId,
                        name = info.followeeName,
                        avatarUrl = info.followeeProfile
                    )
                }
    }

    // 선택된 친구
    var selectedFriend by remember { mutableStateOf(Friend(0, "나")) }
    LaunchedEffect(friends) {
        if (friends.none { it.id == selectedFriend.id }) {
            selectedFriend = friends.firstOrNull() ?: Friend(0, "나")
        }
    }

    // 선택된 친구 바뀌면 비교 데이터 로드(캐시 사용)
    LaunchedEffect(selectedFriend.id) {
        if (selectedFriend.id != 0) viewModel.reloadFriendDaily(selectedFriend.id)
    }

    // ===== 오늘 첫 실행: 추천 미션 시트 자동 노출(하루 1회) =====
    val dayKey = remember { LocalDate.now(ZoneId.of("Asia/Seoul")).toString() }
    var showMissionSheet by rememberSaveable { mutableStateOf(false) }
    var lastTriggeredByDayKey by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.missionReceived, state.missions, dayKey) {
        if (!state.missionReceived && state.missions.isNotEmpty() && lastTriggeredByDayKey != dayKey) {
            showMissionSheet = true
            lastTriggeredByDayKey = dayKey
        }
        if (state.missionReceived) {
            showMissionSheet = false
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

    // ===== Pull-to-refresh 상태 =====
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) isRefreshing = false
    }

    // ---------------- 메인 컨텐츠: LightGray 배경 + PullToRefresh ----------------
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightGray
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refresh()
                // 필요 시 친구 비교 데이터도 리프레시하려면 선택된 친구가 있을 때 호출
                if (selectedFriend.id != 0) viewModel.reloadFriendDaily(selectedFriend.id)
            },
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
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

                // --- 로딩 ---
                if (state.isLoading && state.profile == null) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                }

                // --- 본문: 내 화면 / 친구 화면 ---
                if (selectedFriend.id == 0) {
                    MyScoreSection(score = state.myScore?.savingScore ?: 0)
                    Spacer(Modifier.height(12.dp))

                    // 오늘의 미션
                    TodoSection(
                        missionReceived = state.missionReceived,
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
        }
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

    Log.d("HomeScreen", "사고지점3")
}

/** StreakEntity -> 고정 길이 Boolean 리스트 */
private fun List<StreakEntity>.toDays(totalDays: Int = 365): List<Boolean> {
    if (isEmpty()) return List(totalDays) { false }
    val sliced = take(min(size, totalDays))
    val flags = sliced.map { it.isActive }
    return if (flags.size >= totalDays) flags.take(totalDays)
    else flags + List(totalDays - flags.size) { false }
}
