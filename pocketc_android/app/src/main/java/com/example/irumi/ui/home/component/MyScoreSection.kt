package com.example.irumi.ui.home.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.irumi.core.designsystem.component.tooltip.InfoTooltip
import com.example.irumi.ui.theme.BrandGreen
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScoreSection(
    score: Int,                         // 외부 상태 그대로 사용 (remember 금지)
    titleColor: Color = BrandGreen,
    maxScore: Int = 100                 // 진행바 최대값(필요시 조절)
) {
    val safe = score.coerceAtLeast(0)
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
        Row(
            modifier = Modifier.fillMaxWidth(), // Row가 전체 너비를 차지하도록
            horizontalArrangement = Arrangement.Start, // 왼쪽 정렬
            verticalAlignment = Alignment.CenterVertically // 수직 중앙 정렬
        ) {
            Text(
                text = "내 절약 점수",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF191F28),
                letterSpacing = (-0.5).sp,
                lineHeight = 28.sp
            )
            Spacer(Modifier.size(4.dp)) // 텍스트와 아이콘 버튼 사이 간격

            InfoTooltip(
                "내 점수 계산법",
                "- 오늘의 지출 중 “필수 소비”와 “비필수 소비”의 비율을 봅니다.\n" +
                        "- 필수 소비의 비중이 지난달보다 높아지면 점수가 올라가고, 줄어들면 점수가 내려갑니다."
            )
        }
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
