package com.example.irumi.ui.stats.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.core.mapper.CategoryMapper
import com.example.irumi.core.mapper.CategoryMapper.isMajorFixed
import com.example.irumi.core.state.UiState
import com.example.irumi.data.dto.response.stats.MonthStatsResponse
import com.example.irumi.ui.stats.CategoryList
import com.example.irumi.ui.stats.ExpenseCategory
import com.example.irumi.ui.stats.StatsCard
import com.example.irumi.ui.theme.BrandGreen
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.text.DecimalFormat


@Composable
fun CategoryPieChart(
    stats: UiState<MonthStatsResponse>
) {
    /**
     * 월간 데이터 통계
     * expenseByCategories: 카테고리별 지출
     * totalExpense: 총 지출 금액
     */
    val monthlyStatistics = stats as? UiState.Success<MonthStatsResponse>
    val expenseByCategories = monthlyStatistics?.data?.expenseByCategories?.sortedByDescending { it.expense }
    val totalExpense = monthlyStatistics?.data?.currMonthExpense ?: 1
    val money = remember { DecimalFormat("#,##0원") }

    /**
     * 색상 규칙
     * fixColors: 고정비 카테고리 색상
     * varColors: 변동비 카테고리 색상
     */
    val fixColors = listOf(
        Color(0xFFF1F5F9), // 아주 연한 슬레이트
        Color(0xFFE2E8F0), // 연한 슬레이트
        Color(0xFFCBD5E1), // 라이트 슬레이트
        Color(0xFFE5E7EB), // 연한 그레이
        Color(0xFFF3F4F6), // 아주 연한 그레이
        Color(0xFFDDD6FE), // 연한 바이올렛 그레이
        Color(0xFFE0E7FF), // 연한 인디고 그레이
        Color(0xFFDBEAFE)  // 연한 블루 그레이
    )

    val varColors = listOf(
        Color(0xFF93C5FD), // 파스텔 블루
        Color(0xFF6EE7B7), // 파스텔 에메랄드
        Color(0xFFFDE68A), // 파스텔 앰버
        Color(0xFFFCA5A5), // 파스텔 레드
        Color(0xFFC4B5FD), // 파스텔 바이올렛
        Color(0xFFF9A8D4), // 파스텔 핑크
        Color(0xFF67E8F9), // 파스텔 시안
        Color(0xFFBEF264)  // 파스텔 라임
    )

    val selectedFixColors = fixColors.map { it.copy(alpha = 0.8f) }
    val selectedVarColors = varColors.map { it.copy(alpha = 0.8f) }

    // 카테고리별 색상 매핑을 위한 인덱스 계산
    var fixColorIndex = 0
    var varColorIndex = 0

    var data by remember {
        mutableStateOf(
            expenseByCategories?.take(8)?.map { item ->
                val isFixed = isMajorFixed(item.categoryId)
                val color = if (isFixed) {
                    fixColors.getOrElse(fixColorIndex) { Color.Gray }.also { fixColorIndex++ }
                } else {
                    varColors.getOrElse(varColorIndex) { Color.Gray }.also { varColorIndex++ }
                }
                val selectedColor = if (isFixed) {
                    selectedFixColors.getOrElse(fixColorIndex - 1) { Color.Black }
                } else {
                    selectedVarColors.getOrElse(varColorIndex - 1) { Color.Black }
                }

                Pie(
                    label = "${item.categoryId}",
                    data = if (totalExpense > 0) item.expense.toDouble() / totalExpense else 0.0,
                    color = color,
                    selectedColor = selectedColor
                )
            }
        )
    }

    // 카테고리 리스트 생성 (색상도 동일한 로직으로)
    val categories = mutableListOf<ExpenseCategory>()
    fixColorIndex = 0 // 리셋
    varColorIndex = 0 // 리셋

    expenseByCategories?.forEach { item ->
        val isFixed = isMajorFixed(item.categoryId)
        val color = if (isFixed) {
            fixColors.getOrElse(fixColorIndex) { Color.Gray }.also { fixColorIndex++ }
        } else {
            varColors.getOrElse(varColorIndex) { Color.Gray }.also { varColorIndex++ }
        }

        categories.add(
            ExpenseCategory(
                name = "${CategoryMapper.getMajorName(item.categoryId)}",
                amount = item.expense,
                color = color
            )
        )
    }

    StatsCard(
        title = "카테고리별 지출 분석",
        subtitle = "이번 달 주요 지출 카테고리를 확인하세요",
        content = {
            Column {
                // 지출이 없을 때와 있을 때 조건부 렌더링
                if (totalExpense <= 0 || expenseByCategories.isNullOrEmpty()) {
                    // 빈 상태 메시지
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "💸",
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "지출이 없어요!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191F28),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "이번 달에는 아직 지출 내역이 없습니다",
                                fontSize = 14.sp,
                                color = Color(0xFF8B95A1),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // 차트와 범례를 나란히 배치 (토스 스타일)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 파이 차트
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            PieChart(
                                modifier = Modifier.size(180.dp),
                                data = data ?: emptyList(),
                                onPieClick = { pie ->
                                    val pieIndex = data?.indexOf(pie)
                                    data = data?.mapIndexed { mapIndex, p ->
                                        // 이미 선택된 파이를 다시 클릭하면 선택 해제, 아니면 해당 파이 선택
                                        p.copy(selected = if (pieIndex == mapIndex && p.selected) false else pieIndex == mapIndex)
                                    }
                                },
                                selectedScale = 1.1f, // 토스 스타일 - 덜 튀는 확대
                                scaleAnimEnterSpec = spring<Float>(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                colorAnimEnterSpec = tween(400),
                                colorAnimExitSpec = tween(400),
                                scaleAnimExitSpec = tween(400),
                                spaceDegreeAnimExitSpec = tween(400),
                                style = Pie.Style.Stroke(width = 35.dp) // 도넛 차트로 변경
                            )

                            // 중앙에 총 지출 표시 (토스 스타일)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "총 지출",
                                    fontSize = 12.sp,
                                    color = Color(0xFF8B95A1),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = money.format(totalExpense),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191F28)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        // 범례 (토스 스타일) - 카테고리에 맞는 색상 사용
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            categories.take(4).forEachIndexed { index, category ->
                                val percentage = ((category.amount.toDouble() / totalExpense) * 100).toInt()

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 색상 인디케이터 - 카테고리에 맞는 색상 사용
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(category.color)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = category.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF191F28)
                                        )
                                        Row {
                                            Text(
                                                text = "${percentage}%",
                                                fontSize = 11.sp,
                                                color = Color(0xFF8B95A1)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "•",
                                                fontSize = 11.sp,
                                                color = Color(0xFF8B95A1)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = money.format(category.amount),
                                                fontSize = 11.sp,
                                                color = Color(0xFF8B95A1)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 구분선
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFF2F4F6)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 상세 카테고리 리스트 (기존 CategoryList 컴포넌트 재활용)
                Column {
                    Text(
                        text = "전체 카테고리",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF191F28),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    CategoryList(
                        stats = stats,
                        categories = categories,
                        modifier = Modifier
                    )
                }
            }
        }
    )
}