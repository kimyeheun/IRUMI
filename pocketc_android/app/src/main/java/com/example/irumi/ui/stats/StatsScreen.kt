package com.example.irumi.ui.stats

import android.widget.Toast
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.example.irumi.ui.profile.MyProfile
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import java.text.DecimalFormat
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

        CategoryPieChart(stats = stats)

        PrimaryButton(
            text = if (loading) "로그아웃 중..." else "로그아웃",
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            loading = loading
        )
    }
}

@Composable
fun StatsCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // 헤더 영역
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191F28),
                    letterSpacing = (-0.5).sp,
                    lineHeight = 28.sp
                )

                subtitle?.let {
                    Text(
                        text = it,
                        fontSize = 15.sp,
                        color = Color(0xFF8B95A1),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 6.dp),
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 콘텐츠 영역
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
        subtitle = "이번 달 예산 사용 현황을 확인하세요",
        content = {
            // 포맷터 및 계산
            val money = remember { DecimalFormat("#,##0원") }
            val clampedUsage = usagePercentage.coerceIn(0, 100)
            val progressTarget = (clampedUsage / 100f).coerceIn(0f, 1f)

            // 애니메이션 (초기 로드 시 0에서 시작)
            var hasStarted by remember { mutableStateOf(false) }

            val animatedProgress by animateFloatAsState(
                targetValue = if (hasStarted) progressTarget else 0f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = EaseOutCubic,
                    delayMillis = 200
                ),
                label = "progress"
            )

            // 퍼센트 텍스트 애니메이션
            val animatedPercentage by animateIntAsState(
                targetValue = if (hasStarted) clampedUsage else 0,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = EaseOutCubic,
                    delayMillis = 200
                ),
                label = "percentage"
            )

            // 컴포넌트가 처음 구성될 때 애니메이션 시작
            LaunchedEffect(Unit) {
                hasStarted = true
            }

            // BrandGreen 단일 색상 사용
            val BrandGreen = Color(0xFF4CAF93)

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 큰 퍼센트 표시 (토스 스타일)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${animatedPercentage}%",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen,
                        letterSpacing = (-1).sp
                    )
                }

                // 프로그레스 바 (토스 스타일 - 더 두꺼우면서 깔끔)
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
                            .background(BrandGreen)
                    )
                }

                // 진행률 텍스트 (작게)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "예산 대비 ${animatedPercentage}% 사용",
                        fontSize = 13.sp,
                        color = Color(0xFF8B95A1),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 정보 섹션 (토스 스타일 - 카드형태)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 예산 정보 카드
                    TossStyleInfoRow(
                        label = "설정 예산",
                        value = money.format(budget),
                        iconBg = Color(0xFF3B82F6).copy(alpha = 0.1f),
                        iconText = "💰"
                    )

                    // 지출 정보 카드
                    TossStyleInfoRow(
                        label = "총 지출 금액",
                        value = money.format(currMonthExpense),
                        iconBg = BrandGreen.copy(alpha = 0.1f),
                        iconText = "💳",
                        valueColor = BrandGreen
                    )

                    // 잔여 예산 카드
                    val remaining = budget - currMonthExpense
                    TossStyleInfoRow(
                        label = if (remaining >= 0) "잔여 예산" else "예산 초과",
                        value = if (remaining >= 0) money.format(remaining) else money.format(-remaining),
                        iconBg = if (remaining >= 0) BrandGreen.copy(alpha = 0.1f) else Color(0xFFFF6B6B).copy(alpha = 0.1f),
                        iconText = if (remaining >= 0) "✨" else "⚠️",
                        valueColor = if (remaining >= 0) BrandGreen else Color(0xFFFF6B6B)
                    )
                }
            }
        }
    )
}

@Composable
private fun TossStyleInfoRow(
    label: String,
    value: String,
    iconBg: Color,
    iconText: String,
    valueColor: Color = Color(0xFF191F28)
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFBFBFC),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 아이콘
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iconText,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 라벨
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4E5968),
                    lineHeight = 20.sp
                )
            }

            // 값
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                letterSpacing = (-0.2).sp
            )
        }
    }
}

@Composable
fun MonthChart(
    stats: UiState<MonthStatsResponse>
) {
    val monthlyStatistics = stats as? UiState.Success<MonthStatsResponse>
    val BrandGreen = Color(0xFF4CAF93)

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
        // 숫자만 추출하고 "월" 추가하여 더 예쁘게
        months = data.monthlySavingScoreList.map { "${it.month.split("-")[1]}월" }

        savingPercent = if (data.lastMonthExpense > 0L) {
            ((data.lastMonthExpense - data.currMonthExpense).toDouble() /
                    data.lastMonthExpense.toDouble()) * 100.0
        } else 0.0
    }

    StatsCard(
        title = "절약 점수 추이",
        subtitle = "최근 7개월 절약 성과를 확인해보세요",
        content = {
            Column {
                // 차트 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(vertical = 8.dp)
                ) {
                    LineChart(
                        modifier = Modifier.fillMaxSize(),
                        data = remember(savingScores) {
                            listOf(
                                Line(
                                    label = "절약점수",
                                    values = savingScores,
                                    color = SolidColor(BrandGreen),
                                    curvedEdges = true, // 토스 스타일 - 부드러운 곡선
                                    dotProperties = DotProperties(
                                        enabled = true,
                                        color = SolidColor(Color.White),
                                        strokeWidth = 3.dp,
                                        radius = 5.dp, // 더 큰 점
                                        strokeColor = SolidColor(BrandGreen),
                                    )
                                )
                            )
                        },
                        labelProperties = LabelProperties(
                            enabled = true,
                            labels = months
                        ),
                        curvedEdges = true,
                        maxValue = 100.0
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 구분선
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFF2F4F6)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AchievementMessage(savingPercent)
            }
        }
    )
}

@Composable
private fun AchievementMessage(savingPercent: Double) {
    val BrandGreen = Color(0xFF4CAF93)

    // 절약률에 따른 메시지와 색상
    val (message, messageColor, bgColor) = when {
        savingPercent > 20 -> Triple(
            "훌륭해요! 지난 달보다 ${String.format("%.0f", savingPercent)}% 절약했어요 🎉",
            BrandGreen,
            BrandGreen.copy(alpha = 0.1f)
        )
        savingPercent > 10 -> Triple(
            "좋아요! 지난 달보다 ${String.format("%.0f", savingPercent)}% 절약했어요 👏",
            BrandGreen,
            BrandGreen.copy(alpha = 0.1f)
        )
        savingPercent > 0 -> Triple(
            "지난 달보다 ${String.format("%.0f", savingPercent)}% 절약했어요",
            BrandGreen,
            BrandGreen.copy(alpha = 0.1f)
        )
        savingPercent < -10 -> Triple(
            "지난 달보다 ${String.format("%.0f", -savingPercent)}% 더 지출했어요",
            Color(0xFF8B95A1),
            Color(0xFFF8F9FA)
        )
        else -> Triple(
            "지난 달과 비슷하게 지출했어요",
            Color(0xFF8B95A1),
            Color(0xFFF8F9FA)
        )
    }

    // 토스 스타일 메시지 박스
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘 영역
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(messageColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (savingPercent > 10) "💰" else if (savingPercent > 0) "📊" else "📈",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 메시지
            Text(
                text = message,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = messageColor,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
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
    val expenseByCategories = monthlyStatistics?.data?.expenseByCategories?.sortedByDescending { it.expense }
    val totalExpense = monthlyStatistics?.data?.currMonthExpense ?: 1
    val money = remember { DecimalFormat("#,##0원") }
    val BrandGreen = Color(0xFF4CAF93)

    /**
     * 모던한 그린-블루 계열 색상 팔레트 (토스 스타일)
     */
    val pieColors = listOf(
        BrandGreen,               // 브랜드 그린
        Color(0xFF52B69A),        // 연한 시그린
        Color(0xFF5BC0BE),        // 터쿠아즈
        Color(0xFF6FAADB),        // 연한 블루그린
        Color(0xFF76C7C0),        // 민트그린
        Color(0xFF43AA8B),        // 진한 시그린
        Color(0xFF4D9078),        // 진한 그린
        Color(0xFF3E8E7E)         // 다크 시그린
    )

    val selectedPieColors = pieColors.map { it.copy(alpha = 0.8f) } // 선택시 약간 투명하게

    var data by remember {
        mutableStateOf(
            expenseByCategories?.take(4)?.mapIndexed { index, item ->
                Pie(
                    label = "${item.categoryId}",
                    data = if (totalExpense > 0) item.expense.toDouble() / totalExpense else 0.0,
                    color = pieColors.getOrElse(index) { Color.Gray },
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

                        // 범례 (토스 스타일)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            categories.take(4).forEachIndexed { index, category ->
                                val percentage = ((category.amount.toDouble() / totalExpense) * 100).toInt()

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 색상 인디케이터
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(pieColors.getOrElse(index) { Color.Gray })
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

                // 범례 (토스 스타일)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    categories.take(4).forEachIndexed { index, category ->
                        val percentage = ((category.amount.toDouble() / totalExpense) * 100).toInt()

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 색상 인디케이터
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(pieColors.getOrElse(index) { Color.Gray })
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
    )
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
    val money = remember { DecimalFormat("#,##0원") }
    val BrandGreen = Color(0xFF4CAF93)

    Column {
        // 전체 지출 요약 카드
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = BrandGreen.copy(alpha = 0.05f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📊",
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "전체 지출",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF191F28)
                        )
                        Text(
                            text = "${categories.size}개 카테고리",
                            fontSize = 13.sp,
                            color = Color(0xFF8B95A1),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Text(
                    text = money.format(monthlyStatistics?.data?.currMonthExpense ?: 0L),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    letterSpacing = (-0.3).sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 카테고리 리스트
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            items(categories) { category ->
                CategoryListItem(
                    category = category,
                )
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
