package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.payments.PaymentsDataSource
import com.example.irumi.data.dto.request.PaymentEditRequest
import com.example.irumi.data.dto.response.payments.PaymentCheckRequest
import com.example.irumi.data.dto.response.payments.PaymentDetailResponse
import com.example.irumi.data.mapper.toPaymentEntity
import com.example.irumi.data.mapper.toPaymentsHistoryEntity
import com.example.irumi.domain.entity.PaymentEntity
import com.example.irumi.domain.entity.PaymentsHistoryEntity
import com.example.irumi.domain.repository.PaymentsRepository
import javax.inject.Inject

class PaymentsRepositoryImpl @Inject constructor(
    private val paymentsDataSource: PaymentsDataSource
) : PaymentsRepository {
    override suspend fun getPaymentDetail(transactionId: Int): Result<PaymentEntity> {
        return runCatching { paymentsDataSource.getPaymentDetail(transactionId).data!!.toPaymentEntity() }
    }

    override suspend fun getPayments(): Result<PaymentsHistoryEntity> {
        return runCatching { paymentsDataSource.getPayments().data!!.toPaymentsHistoryEntity() }
    }

    override suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEditRequest
    ): Result<PaymentDetailResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun patchPaymentDetail(transactionId: Int): Result<PaymentCheckRequest> {
        TODO("Not yet implemented")
    }
}