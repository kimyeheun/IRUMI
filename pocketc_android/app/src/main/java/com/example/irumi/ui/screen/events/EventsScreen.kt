package com.example.irumi.ui.screen.events

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.screen.events.event.PuzzleScreen

@Composable
fun EventsScreen(brand: Color) {
    Text("이벤트", fontSize = 28.sp, color = brand)
    PuzzleScreen()
}
