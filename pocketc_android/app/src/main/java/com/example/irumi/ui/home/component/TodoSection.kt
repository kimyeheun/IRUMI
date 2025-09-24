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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * UI용 간단한 미션 아이템 모델.
 * 서버 연동 시 ViewModel에서 이 모델로 변환해서 넘겨주면 됨.
 */
data class TodoItemUi(
    val id: Int,
    val title: String,
    val checked: Boolean
)

@Composable
fun TodoSection(
    items: List<TodoItemUi>? = null,                 // ← 기본값: null이면 플레이스홀더 표시
    isUpdatingId: Int? = null,                       // ← 갱신 중인 항목 id(선택)
    error: String? = null,                           // ← 오류 메시지(선택)
    onToggle: (id: Int, newValue: Boolean) -> Unit = { _, _ -> }, // ← 기본 no-op
    modifier: Modifier = Modifier
) {
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

        // 데이터 없을 때(초기/로딩/미설정) – 가벼운 플레이스홀더
        if (items.isNullOrEmpty()) {
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
                .heightIn(max = 260.dp),           // 내부 스크롤만 생기도록 높이 제한
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
