package com.example.irumi.ui.payments.model

import com.example.irumi.domain.entity.PaymentEntity

sealed class PaymentsListItem {
    data class Header(val date: String, val dailyTotal: Int) : PaymentsListItem()
    data class Payment(val payment: PaymentEntity, val onPaymentItemClick: (Int) -> Unit) : PaymentsListItem()
}
