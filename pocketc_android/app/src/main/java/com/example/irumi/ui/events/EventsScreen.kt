package com.example.irumi.ui.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EventsScreen(brand: Color, viewModel: EventViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // TODO viewmodel 을 안넘기고,, 데이터만 넘기는건?
    when (uiState) {
        is EventUiState.NoRoom -> {
            NoEventScreen()
        }
        is EventUiState.InRoom -> {
            val (roomEntity, eventEntity) = uiState as EventUiState.InRoom
            EventRoomScreen(roomEntity = roomEntity, eventEntity = eventEntity)
        }
        is EventUiState.GameEnd -> {
            val (isSuccess, roomEntity, eventEntity) = uiState as EventUiState.GameEnd
            EventRoomScreen(roomEntity = roomEntity, eventEntity = eventEntity, isSuccess = isSuccess)
        }
    }
}