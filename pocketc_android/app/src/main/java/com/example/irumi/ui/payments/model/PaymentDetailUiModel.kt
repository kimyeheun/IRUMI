package com.example.irumi.ui.payments.model


data class PaymentDetailUiModel(
    val paymentId: Int,
    val date: String,
    val amount: Int,
    val merchantName: String,
    val majorCategoryName: String,
    val subCategoryName: String,
    val isApplied: Boolean,
    val isFixed: Boolean // 고정비 여부
)
