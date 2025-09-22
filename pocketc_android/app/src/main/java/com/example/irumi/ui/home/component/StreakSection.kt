package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

@Composable
fun StreakSection(
    friendName: String? = null,
    totalDays: Int = 365,
    boxSize: Dp = 14.dp,
    boxSpacing: Dp = 3.dp,
    weekSpacing: Dp = 6.dp,
    days: List<Boolean>? = null,
    startWeekdayOffset: Int = 0
) {
    val streakDays = remember(days, totalDays) {
        days ?: List(totalDays) { i -> (i % 3) == 0 } // 예시 데이터
    }
    val weeks = ceil(totalDays / 7.0).toInt()

    Column {
        Text(
            text = friendName?.let { "$it 의 스트릭" } ?: "나의 스트릭",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height((boxSize * 7) + (boxSpacing * 6)),
            horizontalArrangement = Arrangement.spacedBy(weekSpacing)
        ) {
            items(weeks) { weekIndex ->
                val start = weekIndex * 7
                val end = min(start + 7, streakDays.size)
                val weekSlice = if (start < end) streakDays.subList(start, end) else emptyList()

                val leadingEmpty = if (weekIndex == 0) startWeekdayOffset else 0
                val padded: List<Boolean?> = buildList {
                    repeat(leadingEmpty) { add(null) }
                    addAll(weekSlice.map { it })
                    while (size < 7) add(null)
                }.take(7)

                Column(verticalArrangement = Arrangement.spacedBy(boxSpacing)) {
                    repeat(7) { dayRow ->
                        val state = padded[dayRow]
                        Box(
                            modifier = Modifier
                                .size(boxSize)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when (state) {
                                        null -> Color(0xFFEDEDED)
                                        true -> BrandGreen
                                        false -> Color(0xFFDFDFDF)
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}
