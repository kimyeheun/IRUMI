package com.example.irumi.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.data.dto.response.RoomStatus
import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.RoomEntity
import com.example.irumi.domain.repository.EventsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventUiState>(EventUiState.Loading)
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    private val _puzzleImageUrl = MutableStateFlow<String?>(null)
    val puzzleImageUrl: StateFlow<String?> =
        _puzzleImageUrl.asStateFlow()


    val totalPieces: Int
        get() {
            val room = (_uiState.value as? EventUiState.InRoom)?.roomEntity
            return when (room?.maxMembers) {
                2 -> 5 * 5
                3 -> 7 * 7
                5 -> 9 * 9
                else -> 9 * 9
            }
        }

    // 방 생성
    fun createRoom(maxMembers: Int) {
        viewModelScope.launch {
            eventRepository.createEventsRoom(maxMembers)
                .onSuccess { response ->
                    Timber.d("!!! ${maxMembers} createRoom 성공: $response")
                    val (roomEntity, eventEntity) = response
                    _puzzleImageUrl.value = eventEntity.eventImageUrl
                    _uiState.value = EventUiState.InRoom(roomEntity, eventEntity)
                }
                .onFailure {
                    Timber.d("!!! ${maxMembers} createRoom 실패: $it")
                    // TODO: Handle error
                }
        }
    }

    // 방 입장
    fun enterRoom(roomCode: String) {
        viewModelScope.launch {
            eventRepository.enterEventsRoom(roomCode)
                .onSuccess { response ->
                    Timber.d("!!! ${roomCode}enterRoom 성공: $response")
                    val (roomEntity, eventEntity) = response
                    _puzzleImageUrl.value = eventEntity.eventImageUrl
                    _uiState.value = EventUiState.InRoom(roomEntity, eventEntity)
                }
                .onFailure {
                    // TODO: Handle error
                    Timber.d("!!! ${roomCode}enterRoom 실패: $it")
                }
        }
    }

    fun leaveRoom() {
        viewModelScope.launch {
            val currentEventEntity = when (val currentState = _uiState.value) {
                is EventUiState.InRoom -> currentState.eventEntity
                is EventUiState.GameEnd -> currentState.eventEntity
                else -> null
            }
            if (currentEventEntity != null) {
                eventRepository.leaveEventsRoom()
                    .onSuccess {
                        Timber.d("!!! leaveRoom 성공: $it")
                        _uiState.value = EventUiState.NoRoom(eventEntity = currentEventEntity)
                    }
                    .onFailure {
                        Timber.d("!!! leaveRoom 실패: $it")
                    }
            } else {
                Timber.d("!!! leaveRoom 실패: currentEventEntity is null")
                getEventsRoomData() // NoRoom 상태로 만들면서 최신 eventEntity도 가져오도록 유도
            }
        }
    }

    fun fillPuzzle() {
        viewModelScope.launch {
            eventRepository.fillPuzzle()
                .onSuccess { response ->
                    Timber.d("!!! fillPuzzle 성공: $response")
                    if(response.puzzles.size == totalPieces) {
                        getEventsRoomData()
                    }else {
                        val currentState = _uiState.value as EventUiState.InRoom
                        val updatedRoom = currentState.roomEntity.copy(
                            puzzles = response.puzzles,
                            ranks = response.ranks,
                            puzzleAttempts = response.puzzleAttempts
                        )
                        _uiState.value = currentState.copy(
                            roomEntity = updatedRoom
                        )
                    }
                }
                .onFailure {
                    // TODO: Handle error
                    Timber.d("!!! fillPuzzle 실패: $it")
                }
        }
    }

    fun getEventsRoomData() {
        viewModelScope.launch {
            eventRepository.getEventsRoom()
                .onSuccess { response ->
                    val (roomEntity, eventEntity) = response
                    _puzzleImageUrl.value = eventEntity.eventImageUrl
                    Timber.d("!!! getEventsRoomData${response.first?.status} 성공: $response")
                    if(roomEntity != null) {
                        when(roomEntity.status) {
                            RoomStatus.SUCCESS -> {
                                _uiState.value = EventUiState.GameEnd(true, roomEntity, eventEntity)
                            }
                            RoomStatus.IN_PROGRESS -> {
                                _uiState.value = EventUiState.InRoom(roomEntity, eventEntity)
                            }
                            RoomStatus.FAILURE -> {
                                _uiState.value = EventUiState.GameEnd(false, roomEntity, eventEntity)
                            }
                        }
                    }else {
                        _uiState.value = EventUiState.NoRoom(eventEntity)
                    }

                }
                .onFailure {
                    Timber.d("!!! getEventsRoomData 실패: $it")
                    // TODO: Handle error
                }
        }
    }

    fun followUser(userId: Int) {
        viewModelScope.launch {
                // TODO: 여기에 실제 팔로우 API 호출 로직을 구현
                // 예를 들어, userRepository.followUser(userId)
                Timber.d("!!! EventViewModel Attempting to follow user: $userId")
                // _followResult.value = Result.success(Unit) // 예시: 성공 상태 업데이트
            // snackber
        }
    }
}

sealed class EventUiState {
    object Loading : EventUiState() // TODO 연결
    data class NoRoom(val eventEntity: EventEntity) : EventUiState()
    data class InRoom(val roomEntity: RoomEntity, val eventEntity: EventEntity) : EventUiState()
    data class GameEnd(
        val isSuccess: Boolean,
        val roomEntity: RoomEntity,
        val eventEntity: EventEntity
    ) : EventUiState()
    data class Error(val message: String) : EventUiState()
}