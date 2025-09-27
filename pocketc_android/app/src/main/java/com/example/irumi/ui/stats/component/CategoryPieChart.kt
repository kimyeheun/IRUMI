package com.example.irumi.ui.stats.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.irumi.core.mapper.CategoryMapper
import com.example.irumi.core.mapper.CategoryMapper.getMajorName
import com.example.irumi.core.mapper.CategoryMapper.isMajorFixed
import com.example.irumi.core.state.UiState
import com.example.irumi.data.dto.response.stats.MonthStatsResponse
import com.example.irumi.ui.stats.CategoryList
import com.example.irumi.ui.stats.ExpenseCategory
import com.example.irumi.ui.stats.StatsCard
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.text.DecimalFormat

/**
 * 카테고리별 지출을 파이 차트로 시각화하는 컴포넌트
 *
 * 주요 기능:
 * - 일반 모드: 모든 카테고리를 다양한 색상으로 표시
 * - 구분 모드: 고정비는 회색, 변동비는 단일 색상으로 구분 표시
 * - 파이 차트 클릭으로 개별 항목 강조
 * - 상위 4개 카테고리 범례와 전체 카테고리 리스트 제공
 */
@Composable
fun CategoryPieChart(
    stats: UiState<MonthStatsResponse>
) {
    /** 데이터 추출 */
    val monthlyStatistics = stats as? UiState.Success<MonthStatsResponse>
    val expenseByCategories = monthlyStatistics?.data?.expenseByCategories?.sortedByDescending { it.expense }
    val totalExpense = monthlyStatistics?.data?.currMonthExpense ?: 1
    val money = remember { DecimalFormat("#,##0원") }

    /** 색상 팔레트 정의 */
    val varColors = listOf(
        Color(0xFF93C5FD), Color(0xFF6EE7B7), Color(0xFFFDE68A), Color(0xFFFCA5A5),
        Color(0xFFC4B5FD), Color(0xFFF9A8D4), Color(0xFF67E8F9), Color(0xFFBEF264)
    )
    val fixedColor = Color(0xFFE5E7EB) // 고정비용 회색
    val selectedFixedColor = Color(0xFFD1D5DB) // 고정비용 선택시 색상

    /** 상태 관리 */
    var isColorModeEnabled by remember { mutableStateOf(false) } // 구분 모드 토글
    var selectedPieIndex by remember { mutableStateOf(-1) } // 선택된 파이 조각 인덱스

    /** 파이 차트 데이터 생성 */
    val data = remember(isColorModeEnabled, expenseByCategories) {
        expenseByCategories?.take(8)?.mapIndexed { index, item ->
            val isFixed = isMajorFixed(item.categoryId)
            val baseColor = varColors.getOrElse(index) { Color.Gray }

            val color = when {
                isColorModeEnabled && isFixed -> fixedColor
                isColorModeEnabled && !isFixed -> varColors[0] // 변동비는 단일 색상
                else -> baseColor // 일반 모드는 인덱스별 색상
            }

            val selectedColor = when {
                isColorModeEnabled && isFixed -> selectedFixedColor
                isColorModeEnabled && !isFixed -> varColors[0].copy(alpha = 0.8f)
                else -> baseColor.copy(alpha = 0.8f)
            }

            Pie(
                label = "${item.categoryId}",
                data = if (totalExpense > 0) item.expense.toDouble() / totalExpense else 0.0,
                color = color,
                selectedColor = selectedColor
            )
        } ?: emptyList()
    }

    /** 카테고리 리스트 생성 */
    val categories = remember(isColorModeEnabled, expenseByCategories) {
        expenseByCategories?.mapIndexed { index, item ->
            val isFixed = isMajorFixed(item.categoryId)
            val baseColor = varColors.getOrElse(index) { Color.Gray }

            val color = when {
                isColorModeEnabled && isFixed -> fixedColor
                isColorModeEnabled && !isFixed -> varColors[0]
                else -> baseColor
            }

            ExpenseCategory(
                name = getMajorName(item.categoryId)!!,
                amount = item.expense,
                color = color
            )
        } ?: emptyList()
    }

    StatsCard(
        title = "카테고리별 지출 분석",
        subtitle = "이번 달 주요 지출 카테고리를 확인하세요",
        content = {
            Column {
                /** 구분 모드 토글 버튼 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clickable { isColorModeEnabled = !isColorModeEnabled }
                            .background(
                                color = if (isColorModeEnabled) Color(0xFFE0E7FF) else Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isColorModeEnabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = if (isColorModeEnabled) Color(0xFF4F46E5) else Color(0xFF6B7280),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isColorModeEnabled) "구분 모드" else "일반 모드",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isColorModeEnabled) Color(0xFF4F46E5) else Color(0xFF6B7280)
                            )
                        }
                    }
                }

                /** 빈 상태 또는 차트 표시 */
                if (totalExpense <= 0 || expenseByCategories.isNullOrEmpty()) {
                    EmptyExpenseState()
                } else {
                    PieChartWithLegend(
                        data = data,
                        categories = categories,
                        totalExpense = totalExpense,
                        money = money,
                        selectedPieIndex = selectedPieIndex,
                        onPieClick = { pieIndex ->
                            selectedPieIndex = if (pieIndex == selectedPieIndex) -1 else pieIndex
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF2F4F6))
                Spacer(modifier = Modifier.height(24.dp))

                /** 전체 카테고리 리스트 */
                CategorySection(
                    isColorModeEnabled = isColorModeEnabled,
                    stats = stats,
                    categories = categories,
                    fixedColor = fixedColor,
                    varColor = varColors[0]
                )
            }
        }
    )
}

/**
 * 지출이 없을 때 표시되는 빈 상태 컴포넌트
 */
@Composable
private fun EmptyExpenseState() {
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
            Text(text = "💸", fontSize = 48.sp, modifier = Modifier.padding(bottom = 16.dp))
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
}

/**
 * 파이 차트와 범례를 표시하는 컴포넌트
 */
@Composable
private fun PieChartWithLegend(
    data: List<Pie>,
    categories: List<ExpenseCategory>,
    totalExpense: Int,
    money: DecimalFormat,
    selectedPieIndex: Int,
    onPieClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        /** 파이 차트 */
        Box(
            modifier = Modifier
                .size(200.dp)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            PieChart(
                modifier = Modifier.size(180.dp),
                data = data.mapIndexed { index, pie ->
                    pie.copy(selected = index == selectedPieIndex)
                },
                onPieClick = { pie ->
                    val pieIndex = data.indexOf(pie)
                    onPieClick(pieIndex)
                },
                selectedScale = 1.1f,
                scaleAnimEnterSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                colorAnimEnterSpec = tween(400),
                colorAnimExitSpec = tween(400),
                scaleAnimExitSpec = tween(400),
                spaceDegreeAnimExitSpec = tween(400),
                style = Pie.Style.Stroke(width = 35.dp)
            )

            /** 중앙 총 지출 표시 */
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

        /** 범례 (상위 4개) */
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.take(4).forEach { category ->
                val percentage = ((category.amount.toDouble() / totalExpense) * 100).toInt()

                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            Text("${percentage}%", fontSize = 11.sp, color = Color(0xFF8B95A1))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("•", fontSize = 11.sp, color = Color(0xFF8B95A1))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(money.format(category.amount), fontSize = 11.sp, color = Color(0xFF8B95A1))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 전체 카테고리 섹션 (범례 + 리스트)
 */
@Composable
private fun CategorySection(
    isColorModeEnabled: Boolean,
    stats: UiState<MonthStatsResponse>,
    categories: List<ExpenseCategory>,
    fixedColor: Color,
    varColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "전체 카테고리",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF191F28)
            )

            /** 구분 모드일 때 범례 표시 */
            if (isColorModeEnabled) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = fixedColor, text = "고정비")
                    LegendItem(color = varColor, text = "변동비")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CategoryList(
            stats = stats,
            categories = categories,
            modifier = Modifier
        )
    }
}

/**
 * 범례 아이템 (색상 + 텍스트)
 */
@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF8B95A1)
        )
    }
}