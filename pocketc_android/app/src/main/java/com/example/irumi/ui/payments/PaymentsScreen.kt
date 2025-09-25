package com.example.irumi.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irumi.core.designsystem.component.dialog.TwoButtonDialog
import com.example.irumi.ui.payments.model.PaymentDetailUiModel
import com.example.irumi.ui.payments.model.PaymentsByDay
import com.example.irumi.ui.payments.model.PaymentsListItem
import com.example.irumi.ui.payments.model.PaymentsUiState
import com.example.irumi.ui.theme.BrandGreen
import timber.log.Timber
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

// 토스 스타일 컬러팔레트
object TossColors {
    val Primary = BrandGreen//Color(0xFF3182F6)
    val Secondary = Color(0xFF1B64DA)
    val Success = Color(0xFF0E7A0B)
    val Error = Color(0xFFF04438)
    val Warning = Color(0xFFF79009)
    val Surface = Color(0xFFFBFBFB)
    val Background = Color.White
    val OnSurface = Color(0xFF191F28)
    val OnSurfaceVariant = Color(0xFF6B7684)
    val Outline = Color(0xFFE5E8EB)
    val OutlineVariant = Color(0xFFF2F4F6)
}

@Composable
fun PaymentRoute(
    paddingValues: PaddingValues,
    viewModel: PaymentsViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val uiState by viewModel.paymentsUiState.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMonthTransactions()
    }

    LaunchedEffect(viewModel) {
        viewModel.navigationEffect.collect { effect ->
            when (effect) {
                is PaymentsNavigationEffect.NavigateToDetail -> {
                    onNavigateToDetail(effect.paymentId)
                }
            }
        }
    }

    PaymentsScreen(
        uiState = uiState,
        selectedMonth = selectedMonth,
        paddingValues = paddingValues,
        onPaymentItemClick = viewModel::onPaymentItemClick,
        onLeftArrowClick = viewModel::selectPreviousMonth,
        onRightArrowClick = viewModel::selectNextMonth,
        onPaymentCheckClick = { paymentId, onFailure ->
            viewModel.onPaymentCheckClick(paymentId, onFailure)
        },
        onRefresh = viewModel::getMonthTransactions
    )
}

@Composable
fun PaymentsScreen(
    uiState: PaymentsUiState,
    selectedMonth: YearMonth,
    paddingValues: PaddingValues,
    onPaymentItemClick: (Int) -> Unit,
    onLeftArrowClick: () -> Unit,
    onRightArrowClick: () -> Unit,
    onPaymentCheckClick: (paymentId: Int, onFailure: () -> Unit) -> Unit,
    onRefresh: () -> Unit = {}
) {
    val displayMonthFormatter = DateTimeFormatter.ofPattern("M월", Locale.KOREA)
    val currentMonthDisplay = selectedMonth.format(displayMonthFormatter)

    var isRefreshing by remember { mutableStateOf(false) }

    // 새로고침 완료 처리
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
            //.padding(paddingValues),
        color = TossColors.Surface
    ) {
        Column {
            // 상단바
            MonthNavigationBar(
                currentMonth = currentMonthDisplay,
                onPreviousClick = onLeftArrowClick,
                onNextClick = onRightArrowClick,
                yearMonth = selectedMonth
            )

            // 당기면 새로고침 가능한 결제내역 리스트
            PullRefreshContent(
                isRefreshing = isRefreshing,
                modifier = Modifier.fillMaxSize(),
                onRefresh = {
                    Timber.d("!!! 새로고침 트리거??")
                    isRefreshing = true
                    onRefresh()
                },
                 content = {
                     when {
                         uiState.isLoading && uiState.groupedTransactions.isEmpty() -> {
                             LoadingView()
                         }

                         uiState.error != null -> {
                             ErrorView(error = uiState.error)
                         }

                         else -> {
                             PaymentsList(
                                 monthlyTotal = uiState.monthlyTotal,
                                 groupedTransactions = uiState.groupedTransactions,
                                 onPaymentItemClick = onPaymentItemClick,
                                 onPaymentCheckClick = onPaymentCheckClick
                             )
                         }
                     }
                 }
            )
        }
    }
}

// 재사용 가능한 토스 스타일 월 네비게이션 바
@Composable
fun MonthNavigationBar(
    currentMonth: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    yearMonth: YearMonth
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TossColors.Background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onPreviousClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "이전 달",
                    tint = TossColors.OnSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = currentMonth,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TossColors.OnSurface
            )

            if(yearMonth.isBefore(YearMonth.now())) {
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "다음 달",
                        tint = TossColors.OnSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }else {
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
    }
}

// 재사용 가능한 당기면 새로고침 컴포넌트
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefreshContent(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    onRefresh: () -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        content()
    }
}

@Composable
fun MonthSummaryCard(
    monthlyTotal: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TossColors.Background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "이번 달 지출",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF191F28)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${String.format("%,d", monthlyTotal)}원",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TossColors.OnSurface
            )
        }
    }
}

@Composable
fun PaymentsList(
    monthlyTotal: Int,
    groupedTransactions: List<PaymentsByDay>,
    onPaymentItemClick: (Int) -> Unit,
    onPaymentCheckClick: (paymentId: Int, onFailure: () -> Unit) -> Unit
) {
    val flattenedList = remember(groupedTransactions) {
        val list = mutableListOf<PaymentsListItem>()
        groupedTransactions.forEach { paymentByDay ->
            list.add(PaymentsListItem.Header(paymentByDay.date, paymentByDay.dailyTotal))
            paymentByDay.payments.forEach { payment ->
                list.add(PaymentsListItem.Payment(payment, onPaymentItemClick))
            }
        }
        list
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // 월 지출 요약 카드
            MonthSummaryCard(
                monthlyTotal = monthlyTotal,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        items(
            items = flattenedList,
            key = { item ->
                when (item) {
                    is PaymentsListItem.Header -> "header-${item.date}"
                    is PaymentsListItem.Payment -> "payment-${item.payment.paymentId}"
                }
            }
        ) { item ->
            when (item) {
                is PaymentsListItem.Header -> DayHeader(
                    date = item.date
                )
                is PaymentsListItem.Payment -> PaymentItem(
                    payment = item.payment,
                    onClick = { item.onPaymentItemClick(item.payment.paymentId) },
                    onPaymentCheckClick = onPaymentCheckClick
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun DayHeader(date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = TossColors.OnSurface
        )
    }
}

@Composable
fun PaymentItem(
    payment: PaymentDetailUiModel,
    onClick: () -> Unit,
    onPaymentCheckClick: (paymentId: Int, onFailure: () -> Unit) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var locallyApplied by remember(payment.paymentId, payment.isApplied) {
        mutableStateOf(payment.isApplied)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TossColors.Background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 상점 아이콘
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(TossColors.OutlineVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = payment.merchantName.firstOrNull()?.toString() ?: "?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TossColors.OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 결제 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = payment.merchantName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TossColors.OnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${payment.majorCategoryName} | ${payment.subCategoryName}",
                    fontSize = 14.sp,
                    color = TossColors.OnSurfaceVariant
                )
            }

            // 금액 및 반영 버튼
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${String.format("%,d", payment.amount)}원",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TossColors.OnSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (!locallyApplied) {
                    Text(
                        text = "반영하기",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(
                                TossColors.Primary,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                showDialog = true
                            }
                    )
                } else {
                    Text(
                        text = "반영완료",
                        fontSize = 12.sp,
                        color = TossColors.Primary.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(
                                Color.Gray.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    if (showDialog) {
        TwoButtonDialog(
            title = "결제내역을 반영하시겠어요?",
            text = "반영하면 결제 내역이 미션에 반영됩니다.",
            confirmButtonText = "반영하기",
            dismissButtonText = "취소",
            onDismissRequest = {
                showDialog = false
            },
            onConfirmFollow = {
                locallyApplied = true
                showDialog = false

                onPaymentCheckClick(payment.paymentId) {
                    locallyApplied = false
                }
            }
        )
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = TossColors.Primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "결제내역을 불러오는 중...",
                fontSize = 14.sp,
                color = TossColors.OnSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorView(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "😞",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "결제내역을 불러올 수 없어요",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TossColors.OnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                fontSize = 14.sp,
                color = TossColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPaymentsScreen() {
    val sampleState = PaymentsUiState(
        isLoading = false,
        monthlyTotal = 1234567,
        groupedTransactions = listOf(
            PaymentsByDay(
                date = "2025. 09. 12 (금)",
                dailyTotal = 50000,
                payments = listOf(
                    PaymentDetailUiModel(1, "", 25000, "왕", "멍멍", "메가커피", false, false),
                    PaymentDetailUiModel(2, "", 25000, "왕", "멍멍", "메가커피", false, false)
                )
            ),
            PaymentsByDay(
                date = "2025. 09. 11 (목)",
                dailyTotal = 73456,
                payments = listOf(
                    PaymentDetailUiModel(3, "", 73456, "왕", "멍멍", "버거킹 홍대점", true, true,)
                )
            )
        )
    )

    PaymentsScreen(
        uiState = sampleState,
        paddingValues = PaddingValues(),
        onPaymentItemClick = {},
        onLeftArrowClick = {},
        selectedMonth = YearMonth.now(),
        onRightArrowClick = {},
        onPaymentCheckClick = { _, _ -> }
    )
}