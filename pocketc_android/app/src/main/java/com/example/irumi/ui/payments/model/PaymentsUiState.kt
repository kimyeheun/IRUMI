package com.example.irumi.ui.payments.model


data class PaymentsUiState(
    val isLoading: Boolean = true,
    val groupedTransactions: List<PaymentsByDay> = emptyList(),
    val monthlyTotal: Int = 0,
    val error: String? = null
)