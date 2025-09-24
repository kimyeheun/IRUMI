// ui/home/HomeScreen.kt
package com.example.irumi.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irumi.domain.entity.main.StreakEntity
import com.example.irumi.ui.home.component.BadgesSection
import com.example.irumi.ui.home.component.FriendAddSheet
import com.example.irumi.ui.home.component.FriendCompareSection
import com.example.irumi.ui.home.component.FriendList
import com.example.irumi.ui.home.component.MyScoreSection
import com.example.irumi.ui.home.component.StreakSection
import com.example.irumi.ui.home.component.TodoSection
import com.example.irumi.ui.theme.BrandGreen
import kotlin.math.min

data class Friend(val id: Int, val name: String)

@Composable
fun HomeScreen(
    brand: Color = BrandGreen,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // 🔹 lifecycle-aware 수집
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 1) 상단 친구 목록: "나" + 서버 팔로우 목록
    val friends = remember(state.follows) {
        listOf(Friend(0, "나")) + state.follows.map { Friend(it.followUserId, it.nickname) }
    }

    // 2) 선택된 친구 (목록 변경 시 안전 보정)
    var selectedFriend by remember { mutableStateOf(Friend(0, "나")) }
    LaunchedEffect(friends) {
        if (friends.none { it.id == selectedFriend.id }) {
            selectedFriend = friends.firstOrNull() ?: Friend(0, "나")
        }
    }

    // 3) 친구 추가 바텀시트 상태
    var showAddSheet by remember { mutableStateOf(false) }
    var followLoading by remember { mutableStateOf(false) }
    var followError by remember { mutableStateOf<String?>(null) }
    var pendingFollowTargetId by remember { mutableStateOf<Int?>(null) }

    // 4) 롱프레스 언팔 상태
    var pendingUnfollow by remember { mutableStateOf<Friend?>(null) }
    var unfollowLoading by remember { mutableStateOf(false) }
    var unfollowError by remember { mutableStateOf<String?>(null) }

    // ===== 본문 =====
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
            }
        )
        Spacer(Modifier.height(16.dp))

        // --- 로딩 & 에러 ---
        if (state.isLoading && state.profile == null) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }
        state.error?.let { err ->
//            AssistChip(
//                onClick = { viewModel.refresh() },
//                label = {
//                    Text(err.ifBlank { "오류가 발생했어요. 다시 시도" })
//                }
//            )
//            Spacer(Modifier.height(12.dp))
        }

        // --- 본문: 내 화면 / 친구 화면 ---
        if (selectedFriend.id == 0) {
            MyScoreSection(score = state.myScore?.savingScore ?: 0)
            Spacer(Modifier.height(12.dp))

            TodoSection()
            Spacer(Modifier.height(12.dp))

            BadgesSection(badges = state.badges)
            Spacer(Modifier.height(12.dp))

            StreakSection(
                friendName = null,
                days = state.streaks.toDays(),
                totalDays = 365
            )
        } else {
            FriendCompareSection(
                myScore = state.myScore?.savingScore,
                friendScore = null, // TODO: 친구 점수 API 나오면 바인딩
                friendName = selectedFriend.name
            )
            Spacer(Modifier.height(12.dp))

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

        LaunchedEffect(state.follows, state.error, followLoading, showAddSheet, pendingFollowTargetId) {
            if (!showAddSheet || !followLoading) return@LaunchedEffect
            if (state.error != null) {
                followLoading = false
                followError = state.error
                return@LaunchedEffect
            }
            pendingFollowTargetId?.let { id ->
                val exists = state.follows.any { it.followUserId == id }
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

        LaunchedEffect(state.follows, state.error, unfollowLoading, pendingUnfollow) {
            if (!unfollowLoading) return@LaunchedEffect
            if (state.error != null) {
                unfollowLoading = false
                unfollowError = state.error
                return@LaunchedEffect
            }
            val removed = state.follows.none { it.followUserId == friend.id }
            if (removed) {
                unfollowLoading = false
                unfollowError = null
                pendingUnfollow = null
            }
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
