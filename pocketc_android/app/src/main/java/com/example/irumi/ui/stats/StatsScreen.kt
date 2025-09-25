package com.example.irumi.ui.stats

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irumi.core.mapper.CategoryMapper
import com.example.irumi.core.state.UiState
import com.example.irumi.data.dto.response.stats.MonthStatsResponse
import com.example.irumi.ui.auth.AuthViewModel
import com.example.irumi.ui.component.button.PrimaryButton
import com.example.irumi.ui.events.LoadingPlaceholder
import com.example.irumi.ui.theme.BrandGreen
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import java.text.DecimalFormat
import java.util.Locale.KOREA
import kotlin.math.roundToInt

/** 컨테이너: ViewModel과 연결 + 로그아웃 성공 시 콜백 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsRoute(
    brand: Color,
    onLoggedOut: () -> Unit, // 인트로 화면으로 이동
    viewModel: AuthViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel()
) {
    val loading = viewModel.loading
    val error = viewModel.error
    val isLoggedIn = viewModel.isLoggedIn
    val isRefreshing = statsViewModel.isRefreshing.collectAsState().value

    val lifecycleOwner = LocalLifecycleOwner.current
    val stats by statsViewModel.statsUiState.collectAsStateWithLifecycle(lifecycleOwner)

    // 로그아웃 성공 감지 → 외부로 알림
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) onLoggedOut()
    }

    // 에러 토스트
    val ctx = LocalContext.current
    LaunchedEffect(error) {
        error?.let { Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show() }
    }

    when(stats) {
        is UiState.Success -> {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { statsViewModel.refresh() }
            ) {
                StatsScreen(
                    brand = brand,
                    loading = loading,
                    stats = stats,
                    onLogout = { viewModel.logout() }
                )
            }
        }
        is UiState.Empty -> TODO()
        is UiState.Failure -> TODO()
        is UiState.Loading -> {
            LoadingPlaceholder()
        }
    }
}

/** 프리젠테이션: UI만 담당 */
@Composable
fun StatsScreen(
    brand: Color,
    loading: Boolean,
    stats: UiState<MonthStatsResponse>,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Header(stats = stats)

        MonthChart(stats = stats)

        Spacer(Modifier.height(8.dp))

        PrimaryButton(
            text = if (loading) "로그아웃 중..." else "로그아웃",
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            loading = loading
        )

        CategoryPieChart(stats = stats)
    }
}

@Composable
fun StatsCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF191F28)
            )

            if(subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF8B95A1),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}

@Composable
fun Header(
    stats: UiState<MonthStatsResponse>
) {
    val monthStatistics = stats as? UiState.Success<MonthStatsResponse>
    /**
     * 사용 통계 데이터
     * currMonthExpense: 당월 지출액
     * remainBudget: 잔여 예산
     * usagePercentage: 예산 사용 비율
     */
    val budget = monthStatistics?.data?.budget!!
    val currMonthExpense = monthStatistics.data.currMonthExpense
    val usagePercentage: Int =
        if (budget > 0L)
            ((currMonthExpense.toDouble() / budget.toDouble()) * 100.0)
                .coerceIn(0.0, 100.0)
                .roundToInt()
        else 0
    // 상단 여백
    Spacer(modifier = Modifier.height(16.dp))

    StatsCard(
        title = "월간 지출 통계",
        content = {
            // 포맷터 및 계산
            val money = remember { DecimalFormat("#,##0원") }
            val clampedUsage = usagePercentage.coerceIn(0, 100)
            val percentText = "${clampedUsage}%"
            val progressTarget = (clampedUsage / 100f).coerceIn(0f, 1f)

            // 애니메이션
            val animatedProgress by animateFloatAsState(
                targetValue = progressTarget,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
                label = "progress"
            )

            // 동적 색상
            val progressColor = when {
                clampedUsage < 70 -> BrandGreen
                clampedUsage < 90 -> Color(0xFFF59E0B)
                else -> Color(0xFFFF6B6B)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "예산 대비 지출량",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF191F28)
                    )

                    Text(
                        text = percentText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 프로그레스 바
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF2F4F6))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(progressColor)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 예산 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "예산",
                        fontSize = 16.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = money.format(budget),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191F28)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 지출 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "총 지출 금액",
                        fontSize = 16.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = money.format(currMonthExpense),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 잔여 예산
                val remaining = budget - currMonthExpense
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (remaining >= 0) "잔여 예산" else "예산 초과",
                        fontSize = 16.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (remaining >= 0) money.format(remaining) else money.format(-remaining),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (remaining >= 0) BrandGreen else Color(0xFFFF6B6B)
                    )
                }
            }
        }
    )
}

@Composable
fun MonthChart(
    stats: UiState<MonthStatsResponse>
) {
    val monthlyStatistics = stats as? UiState.Success<MonthStatsResponse>
    val dataPointsCount = 7 // lineData.firstOrNull()?.values?.size ?: 0

    // TODO : months 항목을 좀 더 예쁘게 보여줘야 함
    /**
     * 통계 계산
     * savingScores: 월별 절약점수 리스트
     * months: 월 리스트
     * savingPercent: 지난 달 대비 절약 비율
     */
    lateinit var savingScores: List<Double>
    lateinit var months: List<String>
    var savingPercent: Double = 0.0
    monthlyStatistics?.data?.let { data ->
        savingScores = data.monthlySavingScoreList.map { it.savingScore }
        months = data.monthlySavingScoreList.map { it.month.split("-")[1] }

        savingPercent = if (data.lastMonthExpense > 0L) {
            ((data.lastMonthExpense - data.currMonthExpense).toDouble() /
                    data.lastMonthExpense.toDouble()) * 100.0
        } else 0.0
    }
    StatsCard(
        title = "절약 점수 추이",
        content = {
            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                data = remember {
                    listOf(
                        Line(
                            label = "Windows",
                            values = savingScores,
                            color = SolidColor(Color.Blue),
                            curvedEdges = false,
                            dotProperties = DotProperties(
                                enabled = true,
                                color = SolidColor(Color.White),
                                strokeWidth = 4.dp,
                                radius = 3.dp,
                                strokeColor = SolidColor(Color.Gray),
                            )
                        )
                    )
                },
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = months
                ),
                curvedEdges = false,
                maxValue = 100.0
            )

            AchievementMessage(savingPercent)
        }
    )
}

@Composable
fun AchievementMessage(
    percentage: Double,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(12.dp))

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = if (percentage > 0.0) CardDefaults.cardColors(containerColor = Color(0xFF00C73C))
                    else CardDefaults.cardColors(containerColor = Color.Red),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎉",
                fontSize = 18.sp,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                text = if (percentage <= 0.0) "다음엔 좀 더 잘해봐요!" else "전월 대비 ${String.format("%.2f", percentage)}% 절약했어요!",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

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
    val expenseByCategories = monthlyStatistics?.data?.expenseByCategories?.sortedByDescending{it.expense}
    val totalExpense = monthlyStatistics?.data?.currMonthExpense ?: 1

    /**
     * 상위 4개 파이차트의 색상
     * pieColors: 기본 파이 상태 색상
     * selectedPieColors: 선택된 파이의 색상
     * categories: 카테고리 지출 내역(카테고리, 지출액) 리스트
     */
    val pieColors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow)
    val selectedPieColors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow)
    var data by remember {
        mutableStateOf(
            expenseByCategories?.take(4)?.mapIndexed { index, item -> // 상위 4개의 카테고리 조회
                Pie(
                    label = "${item.categoryId}",
                    data = if (totalExpense > 0) item.expense.toDouble() / totalExpense else 0.0,
                    color = pieColors.getOrElse(index) { Color.Gray }, // 기타
                    selectedColor = selectedPieColors.getOrElse(index) { Color.Black }
                )
            }
        )
    }
    val categories = mutableListOf<ExpenseCategory>()
    expenseByCategories?.forEachIndexed { index, item ->
        categories.add(
            ExpenseCategory(
                name = "${CategoryMapper.getMajorName(item.categoryId)}",
                percentage = if (totalExpense > 0) item.expense / totalExpense else 0,
                amount = item.expense,
                color = pieColors.getOrElse(index) { Color.Gray }
            )
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "카테고리별 지출",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF191F28)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PieChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                data = data!!,
                onPieClick = {
                    println("${it.label} Clicked")
                    val pieIndex = data?.indexOf(it)
                    data =
                        data?.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                },
                selectedScale = 1.2f,
                scaleAnimEnterSpec = spring<Float>(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                colorAnimEnterSpec = tween(300),
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                style = Pie.Style.Fill
            )

            CategoryList(stats = stats, categories = categories)
        }
    }
}

data class ExpenseCategory(
    val name: String,
    val percentage: Int,
    val amount: Int,
    val color: Color
)

@Composable
fun CategoryList(
    stats: UiState<MonthStatsResponse>,
    categories: List<ExpenseCategory>,
    modifier: Modifier = Modifier
) {
    val monthlyStatistics = stats as? UiState.Success<MonthStatsResponse>
   Column(
        modifier = Modifier.padding(20.dp)
    ) {
        // 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "전체",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF191F28)
            )
            Text(
                text = "${monthlyStatistics?.data?.currMonthExpense}원",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191F28)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFF2F4F6))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 카테고리 리스트
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.heightIn(max = 400.dp) // 최대 높이 제한
        ) {
            items(categories) { category ->
                CategoryListItem(category = category)
            }
        }
    }
}

@Composable
private fun CategoryListItem(
    category: ExpenseCategory
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽: 컬러 도트 + 카테고리명 + 퍼센트
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 컬러 도트
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        category.color,
                        CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 카테고리명
            Text(
                text = category.name,
                fontSize = 15.sp,
                color = Color(0xFF4E5968)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 퍼센트
            Text(
                text = "${category.percentage}%",
                fontSize = 14.sp,
                color = Color(0xFF8B95A1),
                fontWeight = FontWeight.Medium
            )
        }

        // 오른쪽: 금액
        Text(
            text = "${String.format("%,d", category.amount)} 원",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = category.color
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsScreenPreview() {
    StatsScreen(
        brand = Color(0xFF00C853),
        loading = false,
        stats = UiState.Loading,
        onLogout = {}
    )
}
