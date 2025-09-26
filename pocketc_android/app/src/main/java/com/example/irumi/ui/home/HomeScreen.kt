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
import com.example.irumi.ui.home.component.BadgesSection
import com.example.irumi.ui.home.component.FriendAddSheet
import com.example.irumi.ui.home.component.FriendCompareSection
import com.example.irumi.ui.home.component.FriendList
import com.example.irumi.ui.home.component.MissionPickSheet
import com.example.irumi.ui.home.component.MyScoreSection
import com.example.irumi.ui.home.component.StreakSection
import com.example.irumi.ui.home.component.TodoSection
import com.example.irumi.ui.theme.BrandGreen
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

    // 화면이 다시 보여질 때마다 새로고침
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // "나" + followIds 기반 친구 목록 (닉네임 없음 → placeholder)
    val friends = remember(state.followInfos, state.profile?.profileImageUrl) {
        listOf(
            Friend(0, "나", state.profile?.profileImageUrl) // 내 프로필 URL
        ) + state.followInfos.map { info ->
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

    // 선택된 친구가 바뀌면 비교 데이터 로드 (캐시 사용)
    LaunchedEffect(selectedFriend.id) {
        if (selectedFriend.id != 0) {
            viewModel.reloadFriendDaily(selectedFriend.id) // skipIfCached = true(기본)
        }
    }

    // --- 오늘 첫 실행: 추천 미션 모달 오픈 제어 ---
    val dayKey = remember { LocalDate.now(ZoneId.of("Asia/Seoul")).toString() }
    var showMissionSheet by rememberSaveable { mutableStateOf(false) }
    var submitLoading by remember { mutableStateOf(false) }
    var lastTriggeredByDayKey by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.missionReceived, state.missions, dayKey) {
        if (!state.missionReceived && state.missions.isNotEmpty() && lastTriggeredByDayKey != dayKey) {
            showMissionSheet = true
            lastTriggeredByDayKey = dayKey
        }
        if (state.missionReceived) {
            showMissionSheet = false
            submitLoading = false
        }
    }

    // 친구 추가/삭제 UI 상태
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
        state.error?.let { _ ->
            // 필요 시 에러 UI 노출
        }

        // --- 본문: 내 화면 / 친구 화면 ---
        if (selectedFriend.id == 0) {
            MyScoreSection(score = state.myScore?.savingScore ?: 0)
            Spacer(Modifier.height(12.dp))

            // 미션 바인딩 (오늘의 미션)
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

            Log.d("", "사고지점2")
        } else {
            // 친구 비교 데이터 (없으면 로딩 중)
            val pair = state.friendDaily[selectedFriend.id]
            if (pair == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
                Spacer(Modifier.height(12.dp))
            } else {
                FriendCompareSection(
                    myScore = pair.me.savingScore,          // 내 점수(엔드포인트에서 함께 제공)
                    friendScore = pair.friend.savingScore,  // 친구 점수
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

        // followInfos 기준으로 성공 여부 감지
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

        // followInfos 기준으로 언팔 성공 감지
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

    // ===== 추천 미션 선택 모달 =====
    if (showMissionSheet) {
        MissionPickSheet(
            missions = state.missions,           // GET 결과: 추천 미션들
            isProcessing = submitLoading,
            initiallySelected = emptySet(),
            onDismiss = { showMissionSheet = false }, // "나중에" 닫기
            onConfirm = { selected ->
                submitLoading = true
                viewModel.submitMissions(selected)
            }
        )
    }

    Log.d("", "사고지점3")
}

/** StreakEntity -> 고정 길이 Boolean 리스트 */
private fun List<StreakEntity>.toDays(totalDays: Int = 365): List<Boolean> {
    if (isEmpty()) return List(totalDays) { false }
    val sliced = take(min(size, totalDays))
    val flags = sliced.map { it.isActive }
    return if (flags.size >= totalDays) flags.take(totalDays)
    else flags + List(totalDays - flags.size) { false }
}
