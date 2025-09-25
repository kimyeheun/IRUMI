package com.example.irumi.ui.payments.model

data class PaymentsByDay(
    val date: String,
    val dailyTotal: Int,
    val payments: List<PaymentDetailUiModel>
)