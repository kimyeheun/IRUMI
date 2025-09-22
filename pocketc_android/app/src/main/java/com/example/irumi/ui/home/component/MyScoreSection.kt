package com.example.irumi.ui.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.theme.BrandGreen

@Composable
fun MyScoreSection(score: Int, titleColor: Color = BrandGreen) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = "내 점수", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor)
        Text(text = "${score}점", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
    }
}
