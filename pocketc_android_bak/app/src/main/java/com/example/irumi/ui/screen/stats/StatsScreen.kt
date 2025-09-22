package com.example.irumi.ui.screen.stats

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun StatsScreen(brand: Color) {
    Text("통계", fontSize = 28.sp, color = brand)
}
