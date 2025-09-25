package com.example.irumi.ui.home.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    val myColor = Color(0xFF16A34A)      // 브랜드 그린 톤
    val friendColor = Color(0xFF3B82F6)  // 블루 톤 (가독성 향상)
    val bgNeutral = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .35f)

    // 값 보정(0~100), 로딩 구분
    val my = myScore?.coerceIn(0, 100)
    val friend = friendScore?.coerceIn(0, 100)
    val isMyLoading = my == null
    val isFriendLoading = friend == null

    // 숫자 애니메이션 (로딩이면 0으로)
    val myAnimated by animateIntAsState(targetValue = my ?: 0, label = "myScoreAnim")
    val friendAnimated by animateIntAsState(targetValue = friend ?: 0, label = "friendScoreAnim")

    // 요약문(애니메이션 값을 기반으로)
    val summary = when {
        !isMyLoading && !isFriendLoading -> {
            val diff = myAnimated - friendAnimated
            when {
                diff > 0  -> "내가 ${diff}점 앞서요 🔼"
                diff < 0  -> "$friendName 이(가) ${abs(diff)}점 앞서요 🔽"
                else      -> "동점이에요 🤝"
            }
        }
        !isMyLoading && isFriendLoading -> "친구 점수를 불러오는 중…"
        else                            -> "점수 불러오는 중…"
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
            color = titleColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScorePill(
                label = "나",
                score = if (isMyLoading) null else myAnimated,
                bg = myColor.copy(alpha = .10f),
                fg = myColor
            )
            ScorePill(
                label = friendName,
                score = if (isFriendLoading) null else friendAnimated,
                bg = if (isFriendLoading) bgNeutral else friendColor.copy(alpha = .10f),
                fg = if (isFriendLoading) onSurfaceVariant else friendColor
            )
        }

        Spacer(Modifier.height(10.dp))
        Text(summary, fontSize = 12.sp, color = onSurfaceVariant, maxLines = 2)
        Spacer(Modifier.height(8.dp))

        // 둘 다 있을 때만 막대 비교(가독 + 애니메이션)
        if (!isMyLoading && !isFriendLoading) {
            MiniBarCompare(
                my = myAnimated,
                friend = friendAnimated,
                myColor = myColor,
                friendColor = friendColor
            )
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
        Text(
            label,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
    val myTarget = my.toFloat() / total
    val friendTarget = friend.toFloat() / total

    // 비율 애니메이션
    val myWeight by animateFloatAsState(
        targetValue = max(0.05f, min(0.95f, myTarget)),
        label = "myWeightAnim"
    )
    val friendWeight by animateFloatAsState(
        targetValue = max(0.05f, min(0.95f, friendTarget)),
        label = "friendWeightAnim"
    )

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
                .weight(myWeight)
                .background(myColor)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(friendWeight)
                .background(friendColor)
        )
    }
}
