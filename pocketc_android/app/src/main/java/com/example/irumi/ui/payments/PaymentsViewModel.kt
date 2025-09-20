package com.example.irumi.ui.payments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.core.state.UiState
import com.example.irumi.data.dto.response.PaymentDetailResponse
import com.example.irumi.domain.repository.DummyRepository
import com.example.irumi.model.payments.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val dummyRepository: DummyRepository
) : ViewModel() {

    private val _selectedPaymentId = MutableStateFlow(0)
    val selectedPaymentId = _selectedPaymentId.asStateFlow()
    val categoryMap = mapOf(
        "식비" to listOf("점심", "저녁", "간식", "음료"),
        "교통비" to listOf("대중교통", "택시", "주유"),
        "생활" to listOf("마트/편의점", "쇼핑", "세탁")
    )

    val majorCategories = categoryMap.keys.toList() // 대분류 목록
    private var _state: MutableStateFlow<UiState<PaymentDetailResponse>> = MutableStateFlow(UiState.Loading)
    val state: StateFlow<UiState<PaymentDetailResponse>>
        get() = _state.asStateFlow()

    // 대분류/소분류 상태
    private val _selectedMajorCategory = MutableStateFlow("식비")
    val selectedMajorCategory = _selectedMajorCategory.asStateFlow()

    private val _selectedMinorCategory = MutableStateFlow(
        categoryMap[_selectedMajorCategory.value]?.firstOrNull() ?: ""
    )
    val selectedMinorCategory = _selectedMinorCategory.asStateFlow()

    // todo code 분리
    data class PaymentsByDay(
        val date: String,
        val payments: List<Transaction>
    )

    // UI에 보여줄 그룹화된 결제 내역
    private val _groupedTransactions = MutableStateFlow<List<PaymentsByDay>>(emptyList())
    val groupedTransactions: StateFlow<List<PaymentsByDay>> = _groupedTransactions

    // 총 지출 금액
    private val _monthlyTotal = MutableStateFlow<Int>(0)
    val monthlyTotal: StateFlow<Int> = _monthlyTotal

    init {
        getMonthTransactions("2025-09")
    }

    fun getMonthTransactions(month: String) {
        val dummyTransactions = mutableListOf<Transaction>()

        // 2025-09-11 더미 데이터 10개
        repeat(10) { index ->
            dummyTransactions.add(
                Transaction(
                    transactionId = 501 + index,
                    date = "2025-09-11T14:25:00Z",
                    amount = 13500 + index * 100,
                    majorCategory = 1,
                    subCategory = 2,
                    merchantName = "스타벅스",
                    isApplied = true,
                    isFixed = (index % 2 == 0),
                    createdAt = "2025-09-11T14:25:30Z",
                    updatedAt = "2025-09-11T14:25:30Z"
                )
            )
        }

        // 2025-09-12 더미 데이터 7개
        repeat(7) { index ->
            dummyTransactions.add(
                Transaction(
                    transactionId = 511 + index,
                    date = "2025-09-12T14:25:00Z",
                    amount = 13500 + index * 200,
                    majorCategory = 1,
                    subCategory = 1,
                    merchantName = "메가박스",
                    isApplied = false,
                    isFixed = (index % 3 == 0),
                    createdAt = "2025-09-12T14:25:30Z",
                    updatedAt = "2025-09-12T14:25:30Z"
                )
            )
        }

        // 2025-09-13 더미 데이터 4개
        repeat(4) { index ->
            dummyTransactions.add(
                Transaction(
                    transactionId = 518 + index,
                    date = "2025-09-13T14:25:00Z",
                    amount = 1350 + index * 50,
                    majorCategory = 2,
                    subCategory = 3,
                    merchantName = "분당선",
                    isApplied = true,
                    isFixed = (index % 4 == 0),
                    createdAt = "2025-09-13T14:25:30Z",
                    updatedAt = "2025-09-13T14:25:30Z"
                )
            )
        }

        // todo 서버에 요청 -> 모든 더미 데이터로 totalSpending 계산
        val totalSpending = dummyTransactions.sumOf { it.amount }

        val grouped = groupTransactionsByDate(dummyTransactions)
        _groupedTransactions.value = grouped
        _monthlyTotal.value = totalSpending
    }

    private fun groupTransactionsByDate(transactions: List<Transaction>): List<PaymentsByDay> {
        // 날짜별로 그룹화
        val groupedMap = transactions.groupBy {
            ZonedDateTime.parse(it.date).toLocalDate()
        }

        // 정렬된 Map을 List로 변환
        return groupedMap.entries
            .sortedByDescending { it.key }
            .map { (date, payments) ->
                PaymentsByDay(
                    // todo; util로 빼기
                    date = date.format(DateTimeFormatter.ofPattern("yyyy. MM. dd (E)")),
                    payments = payments
                )
            }
    }

//    var state: MutableStateFlow<UiState<DummyEntity>> = MutableStateFlow(UiState.Loading)
//        private set

//    fun getDummy(page: Int) {
//        viewModelScope.launch {
//            dummyRepository.getDummy(page = page)
//                .onSuccess { response ->
//                    Log.d("viewModel", response.userId.toString())
//                    state.update { UiState.Success(response) } // .value 비교 -> 동시성 문제 해결
//                }
//                .onFailure { error -> // .onFailure 블록 추가
//                    Log.e("getDummy", "데이터 가져오기 실패: ${error.message}")
//                    state.update { UiState.Failure(error.message ?: "알 수 없는 오류") }
//                }
//        }
//    }

    fun getPaymentDetail() {
        Log.d("getPaymentDetail", "${_selectedPaymentId} 호출")
        viewModelScope.launch {
            _state.update {
                UiState.Success(PaymentDetailResponse(
                    transactionId = 501,
                    date = "2025-09-11T14:25:00Z",
                    amount = 13500,
                    majorCategory = 2,
                    subCategory = 3,
                    merchantName = "스타벅스",
                    isApplied = true,
                    isFixed = false,
                    createdAt = "2025-09-11T14:25:30Z",
                    updatedAt = "2025-09-11T14:25:30Z"
                ))
            }
//            paymentRepository.getPaymentDetail(transactionId)
//                .onSuccess { response ->
//                    _state.update {
//
//                        UiState.Success(
//                                response
//                            )
//                    }
//                }
//                .onFailure {
//                    _state.update {
//                            UiState.Failure("서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
//                    }
//                }
        }
    }

    fun onSelectedMajorCategorySelected(category: String) {
        _selectedMajorCategory.value = category
        // 소분류 초기화 로직
        _selectedMinorCategory.value = categoryMap[category]?.firstOrNull() ?: ""
    }

    fun onSelectedMinorCategorySelected(category: String) {
        _selectedMinorCategory.value = category
    }

    fun onEditClick() {
        // TODO 수정 서버통신
        viewModelScope.launch {
            _state.update { currentState ->
                if (currentState is UiState.Success) {
                    // UI에서 받은 문자열 카테고리를 서버가 이해하는 Int 값으로 변환
                    val updatedData = currentState.data.copy(
                        majorCategory = majorCategories.indexOf(_selectedMajorCategory.value) + 1,
                        subCategory = categoryMap[_selectedMajorCategory.value]?.indexOf(_selectedMinorCategory.value)
                            ?.plus(1) ?: 0
                    )
                    // TODO 서버에 업데이트 요청을 보냄 (예시)
                    // paymentRepository.updatePaymentDetail(updatedData)

                    // 성공하면 새로운 상태를 반환
                    UiState.Success(updatedData)
                } else {
                    // 현재 상태가 Success가 아니면 아무것도 하지 않고 반환
                    currentState
                }
            }
        }
    }
}