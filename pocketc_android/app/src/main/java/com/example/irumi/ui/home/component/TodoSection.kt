package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.domain.entity.main.MissionEntity

/**
 * UI용 간단한 미션 아이템 모델.
 */
data class TodoItemUi(
    val id: Int,
    val title: String,
    val checked: Boolean
)

@Composable
fun TodoSection(
    missionReceived: Boolean,
    missions: List<MissionEntity>,
    isUpdatingId: Int? = null,
    error: String? = null,
    onToggle: (id: Int, newValue: Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    // 서버 모델 → UI 모델 매핑
    val items = remember(missions) {
        missions.map {
            TodoItemUi(
                id = it.missionId,
                title = it.mission,
                // 상태/진행도 기준 체크 표시(필요 시 조건 수정)
                checked = it.status.equals("DONE", ignoreCase = true) || it.progress >= 100
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("오늘의 미션", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        // 오류 메시지
        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(6.dp))
        }

        // 아직 선택(확정) 전이면 가이드/플레이스홀더
        if (!missionReceived || items.isEmpty()) {
            Text(
                text = "아침에 추천 미션을 선택하면 여기에 보여줘요!",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
            )
            Spacer(Modifier.height(8.dp))
            repeat(3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7F8FA))
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE6E8EC))
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .height(14.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE6E8EC))
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            return@Column
        }

        // 실제 미션 리스트
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.id }) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7F8FA))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val disabled = isUpdatingId == item.id
                    Checkbox(
                        checked = item.checked,
                        onCheckedChange = { checked ->
                            if (!disabled) onToggle(item.id, checked)
                        },
                        enabled = !disabled
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        color = if (disabled) Color(0xFF9CA3AF) else Color.Unspecified
                    )
                }
            }
        }
    }
}
