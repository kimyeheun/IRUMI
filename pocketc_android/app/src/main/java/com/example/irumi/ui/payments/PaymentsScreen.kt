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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irumi.data.dto.response.Payment
import com.example.irumi.domain.entity.PaymentEntity
import androidx.compose.runtime.remember
import com.example.irumi.ui.payments.model.PaymentsByDay
import com.example.irumi.ui.payments.model.PaymentsListItem
import com.example.irumi.ui.payments.model.PaymentsUiState

@Composable
fun PaymentRoute(
    paddingValues: PaddingValues,
    viewModel: PaymentsViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val uiState by viewModel.paymentsUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEffect.collect {
            when (it) {
                is PaymentsNavigationEffect.NavigateToDetail -> onNavigateToDetail(it.paymentId)
            }
        }
    }

    PaymentsScreen(
        uiState = uiState,
        paddingValues = paddingValues,
        onPaymentItemClick = viewModel::onPaymentItemClick
    )
}

@Composable
fun PaymentsScreen(
    uiState: PaymentsUiState,
    paddingValues: PaddingValues,
    onPaymentItemClick: (Int) -> Unit
) {
    val currentMonth = "2025년 9월" // TODO: ViewModel에서 관리

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
                        modifier = Modifier.size(24.dp).clickable { /* TODO */ }
                    )
                    Text(
                        text = "$currentMonth 결제내역",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "다음달",
                        modifier = Modifier.size(24.dp).clickable { /* TODO */ }
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
                            is PaymentsListItem.Payment -> PaymentItem(payment = item.payment, onClick = { item.onPaymentItemClick(item.payment.paymentId) })
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
    onClick: () -> Unit
) {
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
            if (!payment.isApplied) {
                Button(onClick = { /* TODO: 반영 버튼 클릭 */ }) {
                    Text("반영", fontSize = 12.sp)
                }
            }
        }
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
    PaymentsScreen(uiState = sampleState, paddingValues = PaddingValues(), onPaymentItemClick = {})
}