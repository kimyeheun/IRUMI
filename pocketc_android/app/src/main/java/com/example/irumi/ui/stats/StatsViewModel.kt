package com.example.irumi.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.core.state.UiState
import com.example.irumi.data.dto.response.stats.MonthStatsResponse
import com.example.irumi.domain.repository.StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statsRepository: StatsRepository
) : ViewModel() {
    private val _statsUiState: MutableStateFlow<UiState<MonthStatsResponse>> =
        MutableStateFlow(UiState.Loading)
    val statsUiState = _statsUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        getMonthStatistics()
    }

    /**
     * 통계 조회
     */
    fun getMonthStatistics() {
        viewModelScope.launch {
            _statsUiState.value = UiState.Loading
            statsRepository.getMonthStatistics(LocalDate.now().toString())
                .onSuccess { response ->
                    Timber.d("!!! getMonthStatistics 성공: $response")
                    _statsUiState.value = UiState.Success(response)
                }
                .onFailure {
                    Timber.d("!!! getMonthStatistics 실패: $it")
                    _statsUiState.value = UiState.Failure(it.message ?: "")
                }
        }
    }

    /**
     * 새로고침
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            statsRepository.getMonthStatistics(LocalDate.now().toString())
                .onSuccess { response ->
                    Timber.d("!!! refresh 성공: $response")
                    _statsUiState.value = UiState.Success(response)
                }
                .onFailure {
                    Timber.d("!!! refresh 실패: $it")
                    _statsUiState.value = UiState.Failure(it.message ?: "")
                }
                .also {
                    _isRefreshing.value = false
                }
        }
    }
}