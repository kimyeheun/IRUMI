package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.payments.PaymentsDataSource
import com.example.irumi.data.dto.response.payments.PaymentCheckRequest
import com.example.irumi.data.mapper.toPaymentEditRequest
import com.example.irumi.data.mapper.toPaymentEntity
import com.example.irumi.data.mapper.toPaymentsHistoryEntity
import com.example.irumi.domain.entity.payments.PaymentEntity
import com.example.irumi.domain.entity.payments.PaymentsHistoryEntity
import com.example.irumi.domain.repository.PaymentsRepository
import javax.inject.Inject

class PaymentsRepositoryImpl @Inject constructor(
    private val paymentsDataSource: PaymentsDataSource
) : PaymentsRepository {
    override suspend fun getPaymentDetail(transactionId: Int): Result<PaymentEntity> {
        return runCatching { paymentsDataSource.getPaymentDetail(transactionId).data!!.toPaymentEntity() }
    }

    override suspend fun getPayments(month: String): Result<PaymentsHistoryEntity> {
        return runCatching { paymentsDataSource.getPayments(month).data!!.toPaymentsHistoryEntity() }
    }

    override suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEntity
    ): Result<PaymentEntity> {
        return runCatching { paymentsDataSource.putPaymentDetail(transactionId, request.toPaymentEditRequest()).data!!.toPaymentEntity() }
    }

    override suspend fun patchPaymentDetail(transactionId: Int): Result<PaymentCheckRequest> {
        return runCatching { paymentsDataSource.patchPaymentDetail(transactionId).data!! }
    }
}