package com.example.irumi.ui.payments.model

sealed class PaymentsListItem {
    data class Header(val date: String, val dailyTotal: Int) : PaymentsListItem()
    data class Payment(val payment: PaymentDetailUiModel, val onPaymentItemClick: (Int) -> Unit) : PaymentsListItem()
}
