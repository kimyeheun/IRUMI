package com.example.irumi.ui.screen.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.model.payments.Transaction
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PaymentsScreen(brand: Color) {
    //Text("결제 내역", fontSize = 28.sp, color = brand)
    MonthlyPaymentScreen()
}

@Preview(showBackground = true)
@Composable
fun MonthlyPaymentScreen(viewModel: PaymentsViewModel = viewModel()) {
    // todo -> ViewModel에서 관리할 상태 (예시)
    val currentMonth = "2025년 9월"
    val groupedPayments by viewModel.groupedTransactions.collectAsState()
    val monthlyTotal by viewModel.monthlyTotal.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 상단 네비게이션
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { /* 이전 달 */ }) { Text("<") }
                    Text(
                        text = "$currentMonth 결제내역",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Button(onClick = { /* 다음 달 */ }) { Text(">") }
                }
                // 당월 지출
                Text("당월 지출 : $monthlyTotal", modifier = Modifier.padding(top = 8.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            groupedPayments.forEach { paymentByDay ->
                // 일별 헤더 아이템
                item {
                    DayHeader(date = paymentByDay.date)
                }
                // 일별 결제 내역 아이템들
                items(items = paymentByDay.payments) { payment ->
                    PaymentItem(payment)
                }
            }
        }
    }
}

@Composable
fun DayHeader(date: String) {
    Text(
        text = date,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.LightGray)
    )
}

@Composable
fun PaymentItem(payment: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            // todo; 날짜 util로 시간만 빼서 넣기
            Text(payment.date, fontSize = 14.sp)
            Text(payment.merchantName, fontWeight = FontWeight.SemiBold)
            // todo; category 태그로 넣기
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${payment.amount}원", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            if (!payment.isApplied) {
                Button(onClick = { /* 반영 버튼 클릭 */ }) {
                    Text("반영")
                }
            }
        }
    }
}