package com.example.irumi.ui.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.theme.BrandGreen

@Composable
fun FriendCompareSection(
    myScore: Int,
    friendScore: Int,
    friendName: String,
    titleColor: Color = BrandGreen
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$friendName 와의 비교",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "나", fontWeight = FontWeight.Bold)
                Text(text = "${myScore}점", color = Color.Red, fontSize = 22.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = friendName, fontWeight = FontWeight.Bold)
                Text(text = "${friendScore}점", color = Color.Green, fontSize = 22.sp)
            }
        }
    }
}
