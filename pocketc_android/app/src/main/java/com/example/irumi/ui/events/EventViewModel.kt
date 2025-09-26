package com.example.irumi.ui.events

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.data.dto.response.events.RoomStatus
import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.RoomEntity
import com.example.irumi.domain.repository.EventsRepository
import com.example.irumi.domain.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventsRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventUiState>(EventUiState.Loading)
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    private val _puzzleImageUrl = MutableStateFlow<String?>(null)
    val puzzleImageUrl: StateFlow<String?> =
        _puzzleImageUrl.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    /**
     * 방 생성
     */
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
                    _toastEvent.emit("방 생성에 실패했습니다")
                    // TODO: Handle error
                }
        }
    }

    /**
     * 방 입장
     */
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
                    _toastEvent.emit("방 입장에 실패했습니다")
                }
        }
    }

    /**
     * 방 나가기
     */
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
                        _uiState.value = EventUiState.NoRoom(eventEntity = it)
                    }
                    .onFailure {
                        Timber.d("!!! leaveRoom 실패: $it")
                    }
            } else {
                Timber.d("!!! leaveRoom 실패: currentEventEntity is null")
                _toastEvent.emit("방 나가기에 실패했습니다")
                getEventsRoomData() // NoRoom 상태로 만들면서 최신 eventEntity도 가져오도록 유도
            }
        }
    }

    /**
     * 퍼즐 맞추기
     */
    fun fillPuzzle() {
        viewModelScope.launch {
            eventRepository.fillPuzzle()
                .onSuccess { response ->
                    Timber.d("!!! fillPuzzle 성공: $response")
                    val currentState = _uiState.value as EventUiState.InRoom
                    if(response.puzzles.size == currentState.roomEntity.totalPieces) {
                        getEventsRoomData()
                    }else {
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
                    _toastEvent.emit("퍼즐 맞추기에 실패했습니다")
                }
        }
    }

    /**
     * 이벤트 방 조회
     */
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
                    _toastEvent.emit("이벤트 방 조회에 실패했습니다")
                }
        }
    }

    fun followUser(userId: Int) {
        viewModelScope.launch {
                // 낙관적 업데이트
                updateMemberFriendStatus(userId, true)
                mainRepository.follow(userId)
                    .onSuccess {
                        Timber.d("!!! EventViewModel followUser${userId} 성공: $it")
                        _toastEvent.emit("팔로우에 성공했습니다")
                    }
                    .onFailure {
                        Timber.d("!!! EventViewModel followUser${userId} 실패: $it")
                        _toastEvent.emit("팔로우에 실패했습니다")
                        updateMemberFriendStatus(userId, false)
                    }
        }
    }

    private fun updateMemberFriendStatus(userId: Int, isFriend: Boolean) {
        val currentState = _uiState.value

        // InRoom 또는 GameEnd 상태일 때만 멤버 목록을 업데이트합니다.
        val updatedState = when (currentState) {
            is EventUiState.InRoom -> {
                val updatedMembers = currentState.roomEntity.members.map { member ->
                    if (member.userId == userId) {
                        member.copy(isFriend = isFriend) // 해당 유저의 isFriend 상태만 변경
                    } else {
                        member
                    }
                }
                val updatedRoom = currentState.roomEntity.copy(members = updatedMembers)
                currentState.copy(roomEntity = updatedRoom)
            }

            is EventUiState.GameEnd -> {
                val updatedMembers = currentState.roomEntity.members.map { member ->
                    if (member.userId == userId) {
                        member.copy(isFriend = isFriend)
                    } else {
                        member
                    }
                }
                val updatedRoom = currentState.roomEntity.copy(members = updatedMembers)
                currentState.copy(roomEntity = updatedRoom)
            }
            // 다른 상태일 경우 변경하지 않음
            else -> currentState
        }

        _uiState.value = updatedState
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