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

    private val _uiState = MutableStateFlow<EventUiState>(EventUiState.NoRoom)
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
                    Timber.d("!!! createRoom: $response")
                    val (roomEntity, eventEntity) = response
                    _puzzleImageUrl.value = eventEntity.eventImageUrl
                    _uiState.value = EventUiState.InRoom(roomEntity, eventEntity)
                }
                .onFailure {
                    // TODO: Handle error
                }
        }
    }

    // 방 입장
    fun enterRoom(roomCode: String) {
        viewModelScope.launch {
            eventRepository.enterEventsRoom(roomCode)
                .onSuccess { response ->
                    Timber.d("!!! enterRoom: $response")
                    val (roomEntity, eventEntity) = response
                    _puzzleImageUrl.value = eventEntity.eventImageUrl
                    _uiState.value = EventUiState.InRoom(roomEntity, eventEntity)
                }
                .onFailure {
                    // TODO: Handle error
                }
        }
    }

    fun leaveRoom() {
        viewModelScope.launch {
            eventRepository.leaveEventsRoom()
                .onSuccess {
                    Timber.d("!!! leaveRoom: $it")
                    _uiState.value = EventUiState.NoRoom
                }
                .onFailure {

                }

        }
    }

    fun fillPuzzle() {
        viewModelScope.launch {
            eventRepository.fillPuzzle()
                .onSuccess { response ->
                    Timber.d("!!! fillPuzzle: $response")
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
                }
        }
    }

    fun getEventsRoomData() {
        viewModelScope.launch {
            eventRepository.getEventsRoom()
                .onSuccess { response ->
                    val (roomEntity, eventEntity) = response
                    _puzzleImageUrl.value = eventEntity.eventImageUrl
                    when(response.first.status) {
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
                }
                .onFailure {
                    // TODO: Handle error
                }
        }
    }
}

sealed class EventUiState {
    object NoRoom : EventUiState()
    data class InRoom(val roomEntity: RoomEntity, val eventEntity: EventEntity) : EventUiState()
    data class GameEnd(
        val isSuccess: Boolean,
        val roomEntity: RoomEntity,
        val eventEntity: EventEntity
    ) : EventUiState()
}