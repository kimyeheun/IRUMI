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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irumi.core.designsystem.component.dialog.TwoButtonDialog
import com.example.irumi.domain.entity.payments.PaymentEntity
import com.example.irumi.ui.payments.model.PaymentsByDay
import com.example.irumi.ui.payments.model.PaymentsListItem
import com.example.irumi.ui.payments.model.PaymentsUiState
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    LaunchedEffect(viewModel.navigationEffect) {
        viewModel.navigationEffect.collect { effect ->
            when (effect) {
                is PaymentsNavigationEffect.NavigateToDetail -> onNavigateToDetail(effect.paymentId)
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
        }
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
    onPaymentCheckClick: (paymentId: Int, onFailure: () -> Unit) -> Unit
) {
    // TODO util -> UI 표 비용 날짜 포맷터 (예: "YYYY년 M월") - 월이 한 자리일 때 '0' 없이 표시
    val displayMonthFormatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREA)
    val currentMonthDisplay = selectedMonth.format(displayMonthFormatter)

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "이전달",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                onLeftArrowClick()
                            }
                    )
                    Text(
                        text = "$currentMonthDisplay 결제내역",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "다음달",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                onRightArrowClick()
                            }
                    )
                }
                Text("당월 지출 : ${uiState.monthlyTotal} 원", modifier = Modifier.padding(top = 8.dp))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val flattenedList = remember(uiState.groupedTransactions) {
                    val list = mutableListOf<PaymentsListItem>()
                    uiState.groupedTransactions.forEach { paymentByDay ->
                        list.add(PaymentsListItem.Header(paymentByDay.date, paymentByDay.dailyTotal))
                        paymentByDay.payments.forEach { payment ->
                            list.add(PaymentsListItem.Payment(payment, onPaymentItemClick))
                        }
                    }
                    list
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
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
                            is PaymentsListItem.Header -> DayHeader(date = item.date, dailyTotal = item.dailyTotal)
                            is PaymentsListItem.Payment -> PaymentItem(
                                payment = item.payment,
                                onClick = { item.onPaymentItemClick(item.payment.paymentId) },
                                onPaymentCheckClick = { paymentId, onFailure ->
                                    onPaymentCheckClick(paymentId, onFailure)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayHeader(date: String, dailyTotal: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = date, fontWeight = FontWeight.Bold)
        Text(text = "$dailyTotal 원", fontWeight = FontWeight.SemiBold, color = Color.Gray)
    }
}

@Composable
fun PaymentItem(
    payment: PaymentEntity,
    onClick: () -> Unit,
    onPaymentCheckClick: (paymentId: Int, onFailure: () -> Unit) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var locallyApplied by remember(payment.paymentId, payment.isApplied) { mutableStateOf(payment.isApplied) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = payment.merchantName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${payment.majorCategory} | ${payment.subCategory}", // TODO: 카테고리명으로 변환
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${payment.amount} 원",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            if (!locallyApplied) {
                Button(onClick = {
                    showDialog = true
                }) {
                    Text("반영", fontSize = 12.sp)
                }
            }
        }
    }

    if (showDialog) {
        TwoButtonDialog (
            title = "결제내역을 반영하시겠어요?",
            text = "반영하면 결제 내역에 미션에 반영됩니다.",
            confirmButtonText = "반영",
            dismissButtonText = "취소",
            onDismissRequest = {
                showDialog = false
            },
            onConfirmFollow = {
                //1. 낙관적 업데이트: UI 즉시 변경
                locallyApplied = true
                showDialog = false

                onPaymentCheckClick(payment.paymentId) {
                        locallyApplied = false
                        // TODO: 사용자에게 오류 메시지 표시 (Snackbar 등 PaymentScreen 레벨에서 처리 가능)
                    }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPaymentsScreen() {
    val sampleState = PaymentsUiState(
        isLoading = false,
        monthlyTotal = 123456,
        groupedTransactions = listOf(
            PaymentsByDay(
                date = "2025. 09. 12 (금)",
                dailyTotal = 50000,
                payments = listOf(
                    PaymentEntity(1, "", 25000, 1, 1, "스타벅스", true, false, "", ""),
                    PaymentEntity(2, "", 25000, 1, 2, "메가커피", false, false, "", "")
                )
            ),
            PaymentsByDay(
                date = "2025. 09. 11 (목)",
                dailyTotal = 73456,
                payments = listOf(
                    PaymentEntity(3, "", 73456, 2, 1, "버거킹", true, true, "", "")
                )
            )
        )
    )
    PaymentsScreen(uiState = sampleState, paddingValues = PaddingValues(), onPaymentItemClick = {}, onLeftArrowClick = {}, selectedMonth = YearMonth.now(), onRightArrowClick = {}, onPaymentCheckClick = { _, _ -> })
}