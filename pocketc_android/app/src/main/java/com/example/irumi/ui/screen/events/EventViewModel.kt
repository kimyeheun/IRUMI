package com.example.irumi.ui.screen.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.RoomEntity
import com.example.irumi.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventUiState>(EventUiState.NoRoom)
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    private val _puzzleImageUrl = MutableStateFlow<String?>(null)
    val puzzleImageUrl: StateFlow<String?> =
        _puzzleImageUrl.asStateFlow()


    // ViewModel 생성 시점에 호출
    init {
        // 초기에는 방이 없는 상태로 시작
        // 필요에 따라 방 상태를 확인하는 로직 추가 가능
    }

    // 방 생성
    fun createRoom(maxMembers: Int) {
        viewModelScope.launch {
            // TODO: 방 생성 API 호출
            getEventsRoomData() // 이벤트 룸 데이터 가져오기
        }
    }

    // 방 입장
    fun enterRoom(roomCode: String) {
        viewModelScope.launch {
            // TODO: 방 입장 API 호출
            getEventsRoomData() // 이벤트 룸 데이터 가져오기
        }
    }

    fun getEventsRoomData() {
        viewModelScope.launch {
            val (roomEntity, eventEntity) = eventRepository.getEventsRoomData()
            // TODO onSuccess
            _puzzleImageUrl.value = eventEntity.eventImageUrl
            _uiState.value = EventUiState.InRoom(roomEntity, eventEntity)
        }
    }
}

sealed class EventUiState {
    object NoRoom : EventUiState()
    data class InRoom(val roomEntity: RoomEntity, val eventEntity: EventEntity) : EventUiState()
    data class GameEnd(val isSuccess: Boolean, val roomEntity: RoomEntity, val eventEntity: EventEntity) : EventUiState()
}