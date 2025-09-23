package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "$friendName 와의 비교",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("나", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFF1F2))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) { Text("${myScore}점", color = Color(0xFFEF4444), fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(friendName, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFFDF3))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) { Text("${friendScore}점", color = Color(0xFF16A34A), fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
