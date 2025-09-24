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
import com.example.irumi.domain.entity.main.StreakEntity
import com.example.irumi.ui.home.component.BadgesSection
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
    val state by viewModel.uiState.collectAsState()

    // friends: "나" + follows (UI용 경량 모델)
    val friends = remember(state.follows) {
        listOf(Friend(0, "나")) + state.follows.map { Friend(it.followUserId, it.nickname) }
    }

    // 선택값이 목록 변화에 의해 사라질 수 있으므로 보호
    var selectedFriend by remember { mutableStateOf(Friend(0, "나")) }
    LaunchedEffect(friends) {
        if (friends.isEmpty()) {
            selectedFriend = Friend(0, "나")
        } else if (friends.none { it.id == selectedFriend.id }) {
            selectedFriend = friends.first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 헤더: 친구 목록
        FriendList(
            friends = friends,
            selected = selectedFriend,
            brand = brand,
            onSelect = { selectedFriend = it },
            onAddClick = { /* TODO: 팔로우 추가 */ }
        )
        Spacer(Modifier.height(16.dp))

        // 로딩 & 오류
        if (state.isLoading && state.profile == null) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }
        state.error?.let { err ->
            AssistChip(
                onClick = { viewModel.refresh() },
                label = { Text(err.ifBlank { "오류가 발생했어요. 다시 시도" }) }
            )
            Spacer(Modifier.height(12.dp))
        }

        // 본문
        if (selectedFriend.id == 0) {
            // 내 점수
            MyScoreSection(score = state.myScore?.savingScore ?: 0)
            Spacer(Modifier.height(12.dp))

            // 할 일 (데모)
            TodoSection()
            Spacer(Modifier.height(12.dp))

            // 뱃지
            BadgesSection(badges = state.badges)
            Spacer(Modifier.height(12.dp))

            // 스트릭
            StreakSection(
                friendName = null,
                days = state.streaks.toDays(),
                totalDays = 365
            )
        } else {
            // 친구 비교 (친구 점수 API 미정 → null 처리)
            FriendCompareSection(
                myScore = state.myScore?.savingScore,
                friendScore = null,                 // TODO: 친구 점수 API 나오면 값 바인딩
                friendName = selectedFriend.name
            )
            Spacer(Modifier.height(12.dp))

            // 친구 스트릭도 API 미정 → 내 스트릭 재사용 (향후 교체)
            StreakSection(
                friendName = selectedFriend.name,
                days = state.streaks.toDays(),
                totalDays = 365
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * StreakEntity 리스트를 고정 길이(Boolean)로 변환.
 * 서버가 최신순/과거순을 보장하지 않아도 안전하게 true/false만 추출.
 */
private fun List<StreakEntity>.toDays(totalDays: Int = 365): List<Boolean> {
    if (isEmpty()) return List(totalDays) { false }
    val sliced = take(min(size, totalDays))              // 초과 보호
    val flags = sliced.map { it.isActive }               // 달성 여부만 취함
    return if (flags.size >= totalDays) flags.take(totalDays)
    else flags + List(totalDays - flags.size) { false }  // 부족 시 뒤쪽 패딩
}
