package com.example.irumi.ui.events

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.irumi.ui.theme.LightGray
import androidx.hilt.navigation.compose.hiltViewModel
import timber.log.Timber

@Composable
fun EventsScreen(brand: Color, viewModel: EventViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // TODO viewmodel 을 안넘기고,, 데이터만 넘기는건?
    LaunchedEffect(Unit) {
        Timber.d("!!! EventsScreen LaunchedEffect")
        viewModel.getEventsRoomData()
    }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Timber.d("EventScreen -> uiState: $uiState")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightGray
    ) {
        when (uiState) {
            is EventUiState.Loading -> {
                Timber.d("!!! EventsScreen Loading -> 로딩 구현")
            }

            is EventUiState.NoRoom -> {
                val eventEntity = (uiState as EventUiState.NoRoom).eventEntity
                NoEventScreen(eventEntity = eventEntity)
            }

            is EventUiState.InRoom -> {
                val currentState = uiState as EventUiState.InRoom
                EventRoomScreen(
                    roomEntity = currentState.roomEntity,
                    eventEntity = currentState.eventEntity,
                    isSuccess = null,
                    onRefresh = { viewModel.getEventsRoomData() },
                    onLeaveClick = viewModel::leaveRoom,
                    onFollowClick = viewModel::followUser,
                    onMatchButtonClick = viewModel::fillPuzzle
                )
            }

            is EventUiState.GameEnd -> {
                val currentState = uiState as EventUiState.GameEnd
                EventRoomScreen(
                    roomEntity = currentState.roomEntity,
                    eventEntity = currentState.eventEntity,
                    isSuccess = currentState.isSuccess,
                    onRefresh = { viewModel.getEventsRoomData() },
                    onLeaveClick = viewModel::leaveRoom,
                    onFollowClick = viewModel::followUser,
                    onMatchButtonClick = viewModel::fillPuzzle
                )
            }

            is EventUiState.Error -> {
                TODO()
            }
        }
    }
}