package com.example.irumi.ui.screen.events.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.model.event.PuzzleData
import com.example.irumi.model.event.Puzzle
import com.example.irumi.model.event.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PuzzleViewModel : ViewModel() {

    private val _puzzleImageUrl = MutableStateFlow<String?>(null)
    val puzzleImageUrl: StateFlow<String?> =
        _puzzleImageUrl.asStateFlow()

    private val _puzzleData = MutableStateFlow<PuzzleData?>(null)
    val puzzleData: StateFlow<PuzzleData?> = _puzzleData.asStateFlow()

    // ViewModel 생성 시점에 호출
    init {
        getPuzzleImageUrl()
        getPuzzles()
    }

    fun getPuzzleImageUrl() {
        viewModelScope.launch {
            _puzzleImageUrl.value =
                "https://mblogthumb-phinf.pstatic.net/MjAyMzEwMDhfMjMz/MDAxNjk2NzMyNTA3NzM1.O5iVGUwOEGFbxoqzH9H5H2qwFmbLNdOR7PmuuNE2PMAg.eY7eLpHanrC_AWz-9T2VCZamarnMq_5i6MBHboR2Z1Ug.JPEG.qmfosej/IMG_7989.JPG?type=w800"
        }
    }

    // 퍼즐 데이터
    fun getPuzzles() {
        viewModelScope.launch {
            _puzzleData.value = PuzzleData(
                puzzles = listOf(
                    Puzzle(1, 1, 1, User(123, "절약왕"), null),
                    Puzzle(2, 1, 2, User(124, "소비조절러"), null),
                    Puzzle(3, 1, 3, null, null),
                    Puzzle(4, 1, 4, User(125, "알뜰이"), null),

                    Puzzle(5, 2, 1, null, null),
                    Puzzle(6, 2, 2, User(126, "저축달인"), null),
                    Puzzle(7, 2, 3, User(124, "소비조절러"), null),
                    Puzzle(8, 2, 4, null, null),

                    Puzzle(9, 3, 1, User(123, "절약왕"), null),
                    Puzzle(10, 3, 2, null, null),
                    Puzzle(11, 3, 3, User(127, "소비지킴이"), null),
                    Puzzle(12, 3, 4, null, null),

                    Puzzle(13, 4, 1, null, null),
                    Puzzle(14, 4, 2, User(124, "소비조절러"), null),
                    Puzzle(15, 4, 3, null, null),
                    Puzzle(16, 4, 4, User(128, "금융천재"), null)
                ),
                filledCount = 10,
                totalPieces = 16
            )
        }
    }
}