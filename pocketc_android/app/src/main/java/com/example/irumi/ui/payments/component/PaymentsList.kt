package com.example.irumi.ui.payments.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.payments.TossColors
import com.example.irumi.ui.payments.model.PaymentsByDay
import com.example.irumi.ui.payments.model.PaymentsListItem
import kotlin.collections.forEach

/**
 * 결제내역 리스트 구조
 */
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

/**
 * 이번달 지출 요약
 */
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

/**
 * 날짜 헤더
 */
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
