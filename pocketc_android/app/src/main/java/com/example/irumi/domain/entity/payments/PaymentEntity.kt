package com.example.irumi.domain.entity.payments

import com.example.irumi.core.mapper.CategoryMapper
import com.example.irumi.ui.payments.model.PaymentDetailUiModel

data class PaymentEntity(
    val paymentId: Int,
    val date: String,
    val amount: Int,
    val majorCategory: Int,
    val subCategory: Int,
    val merchantName: String,
    val isApplied: Boolean,
    val isFixed: Boolean,
    val createdAt: String,
    val updatedAt: String
)


fun PaymentEntity.toPaymentDetailUiModel() : PaymentDetailUiModel {
    val majorName = CategoryMapper.getMajorName(majorCategory) ?: "알 수 없음"
    val subName = CategoryMapper.getSubName(subCategory) ?: "알 수 없음"
    return PaymentDetailUiModel(
        paymentId = paymentId,
        date = date,
        amount = amount,
        merchantName = merchantName,
        majorCategoryName = majorName,
        subCategoryName = subName,
        isApplied = isApplied,
        isFixed = isFixed
    )
}