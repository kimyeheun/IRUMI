package com.example.irumi.ui.screen.payments

import androidx.lifecycle.ViewModel
import com.example.irumi.model.payments.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PaymentsViewModel: ViewModel() {

    // todo code 분리
    data class PaymentsByDay(
        val date: String,
        val payments: List<Transaction>
    )
    // UI에 보여줄 그룹화된 결제 내역
    private val _groupedTransactions = MutableStateFlow<List<PaymentsByDay>>(emptyList())
    val groupedTransactions: StateFlow<List<PaymentsByDay>> = _groupedTransactions

    // 총 지출 금액
    private val _monthlyTotal = MutableStateFlow<Int>(0)
    val monthlyTotal: StateFlow<Int> = _monthlyTotal

    init {
        getMonthTransactions("2025-09")
    }

    fun getMonthTransactions(month: String) {
        val dummyTransactions = mutableListOf<Transaction>()

        // 2025-09-11 더미 데이터 10개
        repeat(10) { index ->
            dummyTransactions.add(
                Transaction(
                    transactionId = 501 + index,
                    date = "2025-09-11T14:25:00Z",
                    amount = 13500 + index * 100,
                    majorCategory = 1,
                    subCategory = 2,
                    merchantName = "스타벅스",
                    isApplied = true,
                    isFixed = (index % 2 == 0),
                    createdAt = "2025-09-11T14:25:30Z",
                    updatedAt = "2025-09-11T14:25:30Z"
                )
            )
        }

        // 2025-09-12 더미 데이터 7개
        repeat(7) { index ->
            dummyTransactions.add(
                Transaction(
                    transactionId = 511 + index,
                    date = "2025-09-12T14:25:00Z",
                    amount = 13500 + index * 200,
                    majorCategory = 1,
                    subCategory = 1,
                    merchantName = "메가박스",
                    isApplied = false,
                    isFixed = (index % 3 == 0),
                    createdAt = "2025-09-12T14:25:30Z",
                    updatedAt = "2025-09-12T14:25:30Z"
                )
            )
        }

        // 2025-09-13 더미 데이터 4개
        repeat(4) { index ->
            dummyTransactions.add(
                Transaction(
                    transactionId = 518 + index,
                    date = "2025-09-13T14:25:00Z",
                    amount = 1350 + index * 50,
                    majorCategory = 2,
                    subCategory = 3,
                    merchantName = "분당선",
                    isApplied = true,
                    isFixed = (index % 4 == 0),
                    createdAt = "2025-09-13T14:25:30Z",
                    updatedAt = "2025-09-13T14:25:30Z"
                )
            )
        }

        // todo 서버에 요청 -> 모든 더미 데이터로 totalSpending 계산
        val totalSpending = dummyTransactions.sumOf { it.amount }

        val grouped = groupTransactionsByDate(dummyTransactions)
        _groupedTransactions.value = grouped
        _monthlyTotal.value = totalSpending
    }

    private fun groupTransactionsByDate(transactions: List<Transaction>): List<PaymentsByDay> {
        // 날짜별로 그룹화
        val groupedMap = transactions.groupBy {
            ZonedDateTime.parse(it.date).toLocalDate()
        }

        // 정렬된 Map을 List로 변환
        return groupedMap.entries
            .sortedByDescending { it.key }
            .map { (date, payments) ->
                PaymentsByDay(
                    // todo; util로 빼기
                    date = date.format(DateTimeFormatter.ofPattern("yyyy. MM. dd (E)")),
                    payments = payments
                )
            }
    }
}