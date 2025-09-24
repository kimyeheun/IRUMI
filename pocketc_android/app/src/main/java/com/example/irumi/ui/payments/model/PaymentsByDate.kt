package com.example.irumi.ui.payments.model

import com.example.irumi.domain.entity.payments.PaymentEntity

data class PaymentsByDay(
    val date: String,
    val dailyTotal: Int,
    val payments: List<PaymentEntity>
)