package com.example.irumi.ui.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.core.mapper.CategoryMapper
import com.example.irumi.core.state.UiState
import com.example.irumi.domain.entity.payments.PaymentEntity
import com.example.irumi.domain.entity.payments.toPaymentDetailUiModel
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
import timber.log.Timber
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val paymentsRepository: PaymentsRepository
) : ViewModel() {

    // --- 결제 목록 관련 상태 ---
    private val _paymentsUiState: MutableStateFlow<PaymentsUiState> =
        MutableStateFlow(PaymentsUiState())
    val paymentsUiState = _paymentsUiState.asStateFlow()

    private val _navigationEffect = MutableSharedFlow<PaymentsNavigationEffect>()
    val navigationEffect: SharedFlow<PaymentsNavigationEffect> = _navigationEffect.asSharedFlow()

    // --- 결제 상세 및 수정 관련 상태 ---
    private val _selectedPaymentId = MutableStateFlow(0)

    private val _paymentDetailState: MutableStateFlow<UiState<PaymentEntity>> =
        MutableStateFlow(UiState.Loading)
    val paymentDetailState: StateFlow<UiState<PaymentEntity>> = _paymentDetailState.asStateFlow()

    // UI에 표시될 대분류 이름 목록
    val majorCategoryNames: List<String> = CategoryMapper.majorNameToId.keys.toList()

    // 현재 선택된 "대분류 이름" (UI에서 사용자가 선택한 값)
    private val _selectedMajorCategoryName = MutableStateFlow(majorCategoryNames.firstOrNull() ?: "")
    val selectedMajorCategoryName: StateFlow<String> = _selectedMajorCategoryName.asStateFlow()

    // 현재 선택된 대분류에 따른 "소분류 이름 목록" (UI의 DropdownMenu 등에 사용)
    private val _minorCategoryNameOptions = MutableStateFlow<List<String>>(emptyList())
    val minorCategoryNameOptions: StateFlow<List<String>> = _minorCategoryNameOptions.asStateFlow()

    // 현재 선택된 "소분류 이름" (UI에서 사용자가 선택한 값)
    private val _selectedMinorCategoryName = MutableStateFlow("")
    val selectedMinorCategoryName: StateFlow<String> = _selectedMinorCategoryName.asStateFlow()

    // 선택된 월
    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()


    init {
        // TODO detail로 옮기기
        updateMinorCategoryOptions(_selectedMajorCategoryName.value)
        Timber.d("!!! PaymentsViewModel init -> ${_selectedMajorCategoryName.value}")
    }
    private fun updateMinorCategoryOptions(majorName: String) {
        val majorId = CategoryMapper.getMajorId(majorName)
        if (majorId != null) {
            val minorNames = CategoryMapper.getSubListByMajorId(majorId)
            _minorCategoryNameOptions.value = minorNames
            // 대분류가 변경되면, 소분류 선택도 해당 목록의 첫 번째 항목 또는 빈 문자열로 초기화
            _selectedMinorCategoryName.value = minorNames.firstOrNull() ?: ""
        } else {
            // 유효하지 않은 대분류 이름인 경우, 소분류 옵션을 비움
            _minorCategoryNameOptions.value = emptyList()
            _selectedMinorCategoryName.value = ""
        }
    }

    fun onPaymentItemClick(paymentId: Int) {
        viewModelScope.launch {
            Timber.d("!!! onPaymentItemClick $paymentId")
            _navigationEffect.emit(PaymentsNavigationEffect.NavigateToDetail(paymentId))
        }
    }

    fun getMonthTransactions() {
        viewModelScope.launch {
            _paymentsUiState.update { it.copy(isLoading = true) }
            paymentsRepository.getPayments(year = _selectedMonth.value.year, month = _selectedMonth.value.month.value)
                .onSuccess { paymentsHistory ->
                    Timber.d("!!! getMonthPayments 성공: ${_selectedMonth.value} + $paymentsHistory")
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
                    Timber.d("!!! getMonthPayments 실패: $error")
                    _paymentsUiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "알 수 없는 오류가 발생했습니다."
                        )
                    }
                }
        }
    }

    fun selectPreviousMonth() {
        _selectedMonth.update { it.minusMonths(1) }
        getMonthTransactions()
    }

    fun selectNextMonth() {
        _selectedMonth.update { it.plusMonths(1) }
        getMonthTransactions()
    }

    private fun groupTransactionsByDate(payments: List<PaymentEntity>): List<PaymentsByDay> {
        val formatter = DateTimeFormatter.ofPattern("dd일 E요일", Locale.KOREAN)
        val inputParser = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        return payments.groupBy { paymentEntity ->
            LocalDateTime.parse(paymentEntity.date, inputParser).toLocalDate()
        }
            .mapValues { entry ->
                entry.value.sortedByDescending { paymentEntity ->
                    LocalDateTime.parse(paymentEntity.date, inputParser)
                }
            }
            .entries
            .sortedByDescending { it.key }
            .map { (date, paymentList) ->
                PaymentsByDay(
                    date = date.format(formatter),
                    dailyTotal = paymentList.sumOf { it.amount },
                    payments = paymentList.map { it.toPaymentDetailUiModel() }
                )
            }
    }

    /**
     * 결제 상세 조회
     */
    fun getPaymentDetail(paymentId: Int?) {
        viewModelScope.launch {
            _paymentDetailState.value = UiState.Loading
            paymentsRepository.getPaymentDetail(paymentId!!)
                .onSuccess { response ->
                    Timber.d("!!! getPaymentDetail ${paymentId}성공: $response")
                    _selectedPaymentId.value = paymentId
                    _paymentDetailState.value = UiState.Success(response)
                    onMajorCategoryNameSelected(CategoryMapper.getMajorName(response.majorCategory)!!)
                    onMinorCategoryNameSelected(CategoryMapper.getSubName(response.subCategory)!!)
                    Timber.d("!!! PaymentsViewModel init -> ${_selectedMajorCategoryName.value}")
                }
                .onFailure {
                    Timber.d("!!! getPaymentDetail ${paymentId} 실패: ${it.message}")
                    _paymentDetailState.value = UiState.Failure("서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
                }
        }
    }

    // 사용자가 UI에서 대분류를 선택했을 때 호출
    fun onMajorCategoryNameSelected(majorName: String) {
        if (_selectedMajorCategoryName.value != majorName) {
            _selectedMajorCategoryName.value = majorName
            updateMinorCategoryOptions(majorName)
        }
    }

    // 사용자가 UI에서 소분류를 선택했을 때 호출
    fun onMinorCategoryNameSelected(minorName: String) {
        _selectedMinorCategoryName.value = minorName
    }
    fun onEditClick(updatedAmount: Int) {
        viewModelScope.launch {
            val currentPaymentDetailState = _paymentDetailState.value
            if(currentPaymentDetailState is UiState.Success) {
                // 현재 선택된 이름들로부터 ID를 가져옴
                val majorId = CategoryMapper.getMajorId(_selectedMajorCategoryName.value)
                // CategoryMapper.getSubId는 majorId를 필요로 하지 않음 (소분류 이름이 고유하다고 가정)
                val subId = CategoryMapper.getSubId(_selectedMinorCategoryName.value)

                if(majorId == null || subId == null) {
                    Timber.e("!!! EditClick: Invalid category selection. MajorName: ${_selectedMajorCategoryName.value}, MinorName: ${_selectedMinorCategoryName.value}")
                    _paymentDetailState.update { UiState.Failure("선택된 카테고리가 유효하지 않습니다. 다시 시도해주세요.") }
                    return@launch
                }

                // 현재 majorId에 해당 minorId가 속해있는지 확인
                val validSubCategoriesForMajor = CategoryMapper.getSubListByMajorId(majorId)
                if (!_selectedMinorCategoryName.value.let { name -> validSubCategoriesForMajor.contains(name) }) {
                    Timber.e("!!! EditClick: Minor category '${_selectedMinorCategoryName.value}' does not belong to major category '${_selectedMajorCategoryName.value}'.")
                    _paymentDetailState.update { UiState.Failure("선택된 소분류가 대분류에 속하지 않습니다.") }
                    return@launch
                }

                val originalPaymentData = currentPaymentDetailState.data

                val updatedData = originalPaymentData.copy(
                    majorCategory = majorId,
                    subCategory = subId,
                    amount = updatedAmount,
                    isFixed = CategoryMapper.isSubFixed(subId)
                )

               _paymentDetailState.update { UiState.Loading } // 로딩 상태로 변경

               runCatching {
                   paymentsRepository.putPaymentDetail(updatedData.paymentId, updatedData)
                       .onSuccess {
                           Timber.d("!!! EditPaymentDetail: $updatedData")
                           _paymentDetailState.update { UiState.Success(updatedData) }
                       }
                       .onFailure {
                           Timber.d("!!! EditPaymentDetail 실패: $it")
                           _paymentDetailState.update { UiState.Failure("서버 업데이트 실패") }
                       }
               }
            }
        }
    }

    fun onPaymentCheckClick(
        paymentId: Int,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            paymentsRepository.checkPaymentDetail(paymentId)
                .onSuccess {
                    Timber.d("!!! checkPaymentDetail ${paymentId} 반영 성공: $it")
                    // TODO 200 -> 인지 확인해보자~
                    // 서버 상태와 ViewModel의 주 상태 동기화
                    if(it.status == 200) {
                        _paymentsUiState.update { currentState ->
                            val updatedGroupedTransactions =
                                currentState.groupedTransactions.map { paymentsByDay ->
                                    paymentsByDay.copy(
                                        payments = paymentsByDay.payments.map { payment ->
                                            if (payment.paymentId == paymentId) {
                                                payment.copy(isApplied = true)
                                            } else {
                                                payment
                                            }
                                        }
                                    )
                                }
                            currentState.copy(groupedTransactions = updatedGroupedTransactions)
                        }
                    }
                }
                .onFailure {
                    Timber.d("!!! patchPaymentDetail 반영 실패: $it")
                    onFailure() // UI 되돌리기
                }
        }
    }
}

sealed class PaymentsNavigationEffect {
    data class NavigateToDetail(val paymentId: Int) : PaymentsNavigationEffect()
}