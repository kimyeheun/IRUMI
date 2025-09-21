package com.example.irumi.ui.payments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.core.state.UiState
import com.example.irumi.domain.entity.PaymentEntity
import com.example.irumi.domain.repository.PaymentsRepository
import com.example.irumi.ui.payments.model.PaymentsByDay
import com.example.irumi.ui.payments.model.PaymentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val paymentsRepository: PaymentsRepository
) : ViewModel() {

    private val _paymentsUiState: MutableStateFlow<PaymentsUiState> =
        MutableStateFlow(PaymentsUiState())
    val paymentsUiState = _paymentsUiState.asStateFlow()

    private val _navigationEffect = MutableSharedFlow<PaymentsNavigationEffect>()
    val navigationEffect: SharedFlow<PaymentsNavigationEffect> = _navigationEffect.asSharedFlow()

    private val _selectedPaymentId = MutableStateFlow(0)
    val selectedPaymentId = _selectedPaymentId.asStateFlow()

    private val _paymentDetailState: MutableStateFlow<UiState<PaymentEntity>> =
        MutableStateFlow(UiState.Loading)
    val paymentDetailState: StateFlow<UiState<PaymentEntity>> = _paymentDetailState.asStateFlow()

    val categoryMap = mapOf(
        "식비" to listOf("점심", "저녁", "간식", "음료"),
        "교통비" to listOf("대중교통", "택시", "주유"),
        "생활" to listOf("마트/편의점", "쇼핑", "세탁")
    )
    val majorCategories = categoryMap.keys.toList()

    private val _selectedMajorCategory = MutableStateFlow("식비")
    val selectedMajorCategory = _selectedMajorCategory.asStateFlow()

    private val _selectedMinorCategory = MutableStateFlow(
        categoryMap[_selectedMajorCategory.value]?.firstOrNull() ?: ""
    )
    val selectedMinorCategory = _selectedMinorCategory.asStateFlow()

    init {
        getMonthTransactions("2025-09")
    }

    fun onPaymentItemClick(paymentId: Int) {
        viewModelScope.launch {
            _navigationEffect.emit(PaymentsNavigationEffect.NavigateToDetail(paymentId))
        }
    }

    fun getMonthTransactions(month: String) {
        viewModelScope.launch {
            _paymentsUiState.update { it.copy(isLoading = true) }
            paymentsRepository.getPayments()
                .onSuccess { paymentsHistory ->
                    val grouped = groupTransactionsByDate(paymentsHistory.payments)
                    _paymentsUiState.update {
                        it.copy(
                            isLoading = false,
                            groupedTransactions = grouped,
                            monthlyTotal = paymentsHistory.totalSpending
                        )
                    }
                }
                .onFailure { error ->
                    _paymentsUiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "알 수 없는 오류가 발생했습니다."
                        )
                    }
                }
        }
    }

    private fun groupTransactionsByDate(payments: List<PaymentEntity>): List<PaymentsByDay> {
        val formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd (E)", Locale.KOREAN)
        return payments.groupBy {
            ZonedDateTime.parse(it.date).toLocalDate()
        }
            .mapValues { entry ->
                entry.value.sortedByDescending { ZonedDateTime.parse(it.date) }
            }
            .entries
            .sortedByDescending { it.key }
            .map { (date, paymentList) ->
                PaymentsByDay(
                    date = date.format(formatter),
                    dailyTotal = paymentList.sumOf { it.amount },
                    payments = paymentList
                )
            }
    }

    fun getPaymentDetail() {
        Log.d("getPaymentDetail", "${_selectedPaymentId.value} 호출")
        viewModelScope.launch {
            _paymentDetailState.value = UiState.Loading
            paymentsRepository.getPaymentDetail(_selectedPaymentId.value)
                .onSuccess { response ->
                    _paymentDetailState.value = UiState.Success(response)
                }
                .onFailure {
                    _paymentDetailState.value = UiState.Failure("서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
                }
        }
    }

    fun onSelectedMajorCategorySelected(category: String) {
        _selectedMajorCategory.value = category
        _selectedMinorCategory.value = categoryMap[category]?.firstOrNull() ?: ""
    }

    fun onSelectedMinorCategorySelected(category: String) {
        _selectedMinorCategory.value = category
    }

    fun onEditClick() {
        viewModelScope.launch {
            _paymentDetailState.update { currentState ->
                if (currentState is UiState.Success) {
                    val updatedData = currentState.data.copy(
                        majorCategory = majorCategories.indexOf(_selectedMajorCategory.value) + 1,
                        subCategory = categoryMap[_selectedMajorCategory.value]?.indexOf(_selectedMinorCategory.value)
                            ?.plus(1) ?: 0
                    )
                    // TODO: 서버에 업데이트 요청
                    // paymentsRepository.putPaymentDetail(updatedData)
                    UiState.Success(updatedData)
                } else {
                    currentState
                }
            }
        }
    }
}

sealed class PaymentsNavigationEffect {
    data class NavigateToDetail(val paymentId: Int) : PaymentsNavigationEffect()
}