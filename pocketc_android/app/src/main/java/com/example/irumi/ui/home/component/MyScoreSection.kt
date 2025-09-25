package com.example.irumi.ui.home.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.theme.BrandGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MyScoreSection(
    score: Int,                         // 외부 상태 그대로 사용 (remember 금지)
    titleColor: Color = BrandGreen,
    maxScore: Int = 100                 // 진행바 최대값(필요시 조절)
) {
    val safe = score.coerceAtLeast(100)
    val animated by animateIntAsState(targetValue = safe, label = "myScoreAnim")
    val nf = NumberFormat.getIntegerInstance(Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("내 점수", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
        Spacer(Modifier.height(8.dp))

        Text(
            "${nf.format(animated)}점",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(10.dp))

        // 점수 변화가 있을 때 자연스럽게 보이도록 진행 바 표시
        LinearProgressIndicator(
            progress = (animated / maxScore.toFloat()).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = BrandGreen,
            trackColor = BrandGreen.copy(alpha = 0.2f) // 선택: 트랙은 연한 초록
        )

        Spacer(Modifier.height(6.dp))

        // 가벼운 코멘트(원하면 조건에 따라 문구 바꾸기)
        val tip = when {
            animated >= 90 -> "완벽해요! 지금 페이스를 유지해봐요 💪"
            animated >= 70 -> "좋아요! 조금만 더 힘내볼까요?"
            animated >= 40 -> "천천히 올라가 볼까요?"
            else -> "오늘도 화이팅!"
        }
        Text(tip, fontSize = 12.sp, color = Color(0xFF6B7280))
    }
}
