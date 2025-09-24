package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.theme.BrandGreen
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun FriendCompareSection(
    myScore: Int?,                 // 내 점수 (서버에서 아직 안 왔으면 null)
    friendScore: Int?,             // 친구 점수 (미제공/로딩 시 null)
    friendName: String,
    titleColor: Color = BrandGreen,
    modifier: Modifier = Modifier
) {
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val positive = Color(0xFF16A34A) // 브랜드 그린 톤
    val negative = Color(0xFFEF4444) // 레드 톤
    val bgNeutral = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .35f)

    val my = myScore?.coerceIn(0, 100)
    val friend = friendScore?.coerceIn(0, 100)

    val summary = when {
        my != null && friend != null -> {
            val diff = my - friend
            when {
                diff > 0  -> "내가 ${diff}점 앞서요 🔼"
                diff < 0  -> "$friendName 이(가) ${abs(diff)}점 앞서요 🔽"
                else      -> "동점이에요 🤝"
            }
        }
        my != null && friend == null -> "친구 점수를 불러오는 중…"
        else                         -> "점수 불러오는 중…"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
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
            ScorePill(
                label = "나",
                score = my,
                bg = positive.copy(alpha = .10f),
                fg = positive
            )
            ScorePill(
                label = friendName,
                score = friend,
                bg = if (friend == null) bgNeutral else negative.copy(alpha = .10f),
                fg = if (friend == null) onSurfaceVariant else negative
            )
        }

        Spacer(Modifier.height(10.dp))
        Text(summary, fontSize = 12.sp, color = onSurfaceVariant)
        Spacer(Modifier.height(8.dp))

        if (my != null && friend != null) {
            MiniBarCompare(my, friend, myColor = positive, friendColor = negative)
        }
    }
}

@Composable
private fun ScorePill(
    label: String,
    score: Int?,        // null → 플레이스홀더
    bg: Color,
    fg: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            if (score == null) {
                // 로딩/미제공 플레이스홀더
                Box(
                    Modifier
                        .width(44.dp)
                        .height(20.dp)
                        .alpha(0.35f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE5E7EB))
                )
            } else {
                Text("${score}점", color = fg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MiniBarCompare(
    my: Int,
    friend: Int,
    myColor: Color,
    friendColor: Color
) {
    val total = max(1, my + friend)
    val myWeight = my.toFloat() / total
    val friendWeight = friend.toFloat() / total

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFF1F5F9))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(max(0.05f, min(0.95f, myWeight)))
                .background(myColor)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(max(0.05f, min(0.95f, friendWeight)))
                .background(friendColor)
        )
    }
}
