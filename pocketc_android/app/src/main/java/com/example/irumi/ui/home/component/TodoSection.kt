package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
private fun RewardChip(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(if (selected) Color(0xFFFFF7E6) else Color(0xFFF4F6F8))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color(0xFFF59E0B) else Color(0xFF6B7280))
    }
}

@Composable
fun TodoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 보상/배지 칩
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RewardChip("첫걸음", selected = true)
            RewardChip("일주일", selected = true)
            RewardChip("한달", selected = false)
            RewardChip("100일", selected = false)
        }
        Spacer(Modifier.height(12.dp))

        Text("오늘의 미션", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        // 미션 리스트 (스크롤 영역)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items((1..8).toList()) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7F8FA))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = i == 3,
                        onCheckedChange = {},
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("미션 $i", fontSize = 15.sp)
                }
            }
        }
    }
}
