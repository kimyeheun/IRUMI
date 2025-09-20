package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.PaymentsDataSource
import com.example.irumi.data.dto.request.PaymentEditRequest
import com.example.irumi.data.dto.response.PaymentCheckRequest
import com.example.irumi.data.dto.response.PaymentDetailResponse
import com.example.irumi.data.dto.response.PaymentsResponse
import com.example.irumi.data.mapper.toPaymentEntity
import com.example.irumi.domain.entity.PaymentEntity
import com.example.irumi.domain.repository.PaymentsRepository
import javax.inject.Inject

class PaymentsRepositoryImpl @Inject constructor(
    private val paymentsDataSource: PaymentsDataSource
) : PaymentsRepository {
//    override suspend fun getDummy(page: Int): Result<DummyEntity> =
//        runCatching {
//            dummyDataSource.getDummy(page).toDummyEntity()
//        }

    override suspend fun getPaymentDetail(transactionId: Int): Result<PaymentEntity> =
        runCatching {
            paymentsDataSource.getPaymentDetail(transactionId).data!!.toPaymentEntity()
        }

    override suspend fun getPayments(): Result<PaymentsResponse> {
        TODO("Not yet implemented")
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