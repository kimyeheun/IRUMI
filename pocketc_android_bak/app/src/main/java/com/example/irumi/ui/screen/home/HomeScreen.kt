package com.example.irumi.ui.screen.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.irumi.ui.theme.BrandGreen
import kotlin.math.ceil
import kotlin.math.min

data class Friend(val id: Int, val name: String)

@Composable
fun HomeScreen(brand: Color = BrandGreen) {
    // 샘플 데이터
    val friends = listOf(
        Friend(0, "나"), Friend(1, "민수"), Friend(2, "나연")
    )
    var selectedFriend by remember { mutableStateOf(friends.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 전체 스크롤 허용
            .padding(16.dp)
    ) {
        // 1. 친구 목록 (고정 가로 스크롤)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(friends) { friend ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedFriend = friend }
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(if (selectedFriend == friend) brand else Color.LightGray)
                    )
                    Text(friend.name, fontSize = 14.sp)
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontSize = 24.sp, color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 2. 내 화면인지 친구 화면인지 구분
        if (selectedFriend.id == 0) {
            // === 나의 화면 ===
            MyScoreSection(score = 81)
            Spacer(Modifier.height(12.dp))
            TodoSection()
            Spacer(Modifier.height(16.dp))
            StreakSection()
        } else {
            // === 친구 비교 화면 ===
            FriendCompareSection(myScore = 81, friendScore = 92, friendName = selectedFriend.name)
            Spacer(Modifier.height(16.dp))
            StreakSection(friendName = selectedFriend.name)
        }
    }
}

@Composable
fun MyScoreSection(score: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "내 점수",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BrandGreen
        )
        Text(
            text = "${score}점",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun FriendCompareSection(
    myScore: Int,
    friendScore: Int,
    friendName: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$friendName 와의 비교",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BrandGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "나", fontWeight = FontWeight.Bold)
                Text(text = "${myScore}점", color = Color.Red, fontSize = 22.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = friendName, fontWeight = FontWeight.Bold)
                Text(text = "${friendScore}점", color = Color.Green, fontSize = 22.sp)
            }
        }
    }
}

@Composable
fun TodoSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { /* 탭 전환 */ }, colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)) {
                Text("데일리")
            }
            Button(onClick = { /* 탭 전환 */ }, colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)) {
                Text("주간/월간")
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // 세로 스크롤 영역
        ) {
            items((1..8).toList()) { i ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = i == 3, onCheckedChange = {})
                    Text("미션 $i", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun StreakSection(
    friendName: String? = null,
    totalDays: Int = 365,                 // 전체 일수
    boxSize: Dp = 14.dp,                  // 칸 크기
    boxSpacing: Dp = 3.dp,                // 칸 간격
    weekSpacing: Dp = 6.dp,               // 주(열) 간격
    days: List<Boolean>? = null,          // true=활동, false=미활동 (없으면 예시 데이터)
    startWeekdayOffset: Int = 0           // 주 시작 요일 보정(0=일, 1=월 …) 필요시 사용
) {
    // 데이터 준비(예시: 3일마다 성공)
    val streakDays = remember(days, totalDays) {
        days ?: List(totalDays) { i -> (i % 3) == 0 }
    }

    // 주(열) 개수
    val weeks = ceil(totalDays / 7.0).toInt()

    Column {
        Text(
            text = friendName?.let { "$it 의 스트릭" } ?: "나의 스트릭",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(8.dp))

        // ── 가로: 주(열) ────────────────────────────────────────────────
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height((boxSize * 7) + (boxSpacing * 6)), // 7행 높이
            horizontalArrangement = Arrangement.spacedBy(weekSpacing)
        ) {
            items(weeks) { weekIndex ->
                // 이 주에 들어갈 7일 슬라이스
                val start = weekIndex * 7
                val end = min(start + 7, streakDays.size)
                val weekSlice = if (start < end) streakDays.subList(start, end) else emptyList()

                // 필요시 요일 오프셋 채우기 (주 시작 요일 보정)
                val leadingEmpty = (if (weekIndex == 0) startWeekdayOffset else 0)
                val padded = buildList<Boolean?> {
                    repeat(leadingEmpty) { add(null) }
                    addAll(weekSlice.map { it })
                    while (size < 7) add(null) // 마지막 열 빈칸 채우기
                }.take(7)

                // ── 세로: 요일(행) ────────────────────────────────────────
                Column(
                    verticalArrangement = Arrangement.spacedBy(boxSpacing)
                ) {
                    repeat(7) { dayRow ->
                        val state = padded[dayRow] // Boolean? (null=빈칸)
                        Box(
                            modifier = Modifier
                                .size(boxSize)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when (state) {
                                        null -> Color(0xFFEDEDED)             // 캘린더 없는 칸
                                        true -> BrandGreen                    // 성공
                                        false -> Color(0xFFDFDFDF)           // 실패/미활동
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}