package com.example.irumi.ui.home.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.theme.BrandGreen
import kotlin.math.ceil
import kotlin.math.min

/**
 * 서버에서 내려준 스트릭 Boolean 배열로만 렌더링.
 *
 * @param friendName   섹션 타이틀 우측에 표시할 친구 이름(없으면 "나의 스트릭")
 * @param days         달성 여부(true/false) 리스트. 반드시 실제 데이터 전달
 * @param startWeekdayOffset 첫 주 시작 요일 오프셋(0=일, 1=월 …) 필요한 경우에만 지정
 * @param footerText   하단 설명 문구(연속 N일 등). null이면 숨김
 */
@Composable
fun StreakSection(
    friendName: String? = null,
    days: List<Boolean>,
    startWeekdayOffset: Int = 0,
    boxSize: Dp = 14.dp,
    boxSpacing: Dp = 3.dp,
    weekSpacing: Dp = 6.dp,
    footerText: String? = null,
    totalDays: Int
) {
    // days 가 비어있을 때는 빈 상태 UI만 출력
    val totalDays = days.size
    val weeks = ceil(totalDays / 7.0).toInt().coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = friendName?.let { "$it 의 스트릭" } ?: "나의 스트릭",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height((boxSize * 7) + (boxSpacing * 6)),
            horizontalArrangement = Arrangement.spacedBy(weekSpacing)
        ) {
            items(
                count=weeks,
                key= { weekIndex -> "week_$weekIndex"}
            ) { weekIndex ->
                val start = weekIndex * 7
                val end = min(start + 7, totalDays)
                val weekSlice = if (start < end) days.subList(start, end) else emptyList()

                // 첫 주만 시작 요일 오프셋 적용
                val leadingEmpty = if (weekIndex == 0) startWeekdayOffset else 0
                val padded: List<Boolean?> = buildList {
                    repeat(leadingEmpty) { add(null) }
                    addAll(weekSlice)
                    while (size < 7) add(null)
                }.take(7)

                Column(verticalArrangement = Arrangement.spacedBy(boxSpacing)) {
                    repeat(7) { dayRow ->
                        val state = padded[dayRow]
                        Box(
                            modifier = Modifier
                                .size(boxSize)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (state) {
                                        null  -> Color(0xFFF1F3F5) // 빈칸(패딩)
                                        true  -> BrandGreen        // 달성
                                        false -> Color(0xFFE6E8EC) // 미달
                                    }
                                )
                        )
                    }
                }
            }
        }

        if (!footerText.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Text(footerText, fontSize = 12.sp, color = Color(0xFF6B7280))
            }
        }
    }
}
