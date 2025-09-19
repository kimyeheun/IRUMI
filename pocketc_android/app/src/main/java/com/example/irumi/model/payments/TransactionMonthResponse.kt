package com.example.irumi.model.payments

data class TransactionMonthResponse(
    val transactions: List<Transaction>,
    val totalSpending: Int
)

data class Transaction(
    val transactionId: Int,
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