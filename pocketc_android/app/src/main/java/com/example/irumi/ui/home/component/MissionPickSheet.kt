// ui/home/component/MissionPickSheet.kt
package com.example.irumi.ui.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.irumi.domain.entity.main.MissionEntity
import com.example.irumi.ui.home.MissionPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionPickSheet(
    period: MissionPeriod,                       // 현재 기간(일/주/월)
    onChangePeriod: (MissionPeriod) -> Unit,     // 탭 전환 콜백
    missions: List<MissionEntity>,
    isProcessing: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        sheetState = sheetState
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                when (period) {
                    MissionPeriod.DAILY -> "오늘의 추천 미션"
                    MissionPeriod.WEEKLY -> "이번 주 추천 미션"
                    MissionPeriod.MONTHLY -> "이번 달 추천 미션"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            MissionToggle(period = period, onChange = onChangePeriod)
            Spacer(Modifier.height(12.dp))

            if (missions.isEmpty()) {
                Text("표시할 미션이 없어요.", color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(8.dp))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = missions,
                        key = { it.keyForList() } // ⬅️ 새 스키마 기준 키
                    ) { m ->
                        MissionRow(m)
                        Divider()
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = { if (!isProcessing) onDismiss() },
                    enabled = !isProcessing
                ) { Text("닫기") }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

/** 상단 탭 토글 (일/주/월) */
@Composable
private fun MissionToggle(
    period: MissionPeriod,
    onChange: (MissionPeriod) -> Unit
) {
    val items = listOf(
        MissionPeriod.DAILY to "일간",
        MissionPeriod.WEEKLY to "주간",
        MissionPeriod.MONTHLY to "월간"
    )
    val selectedIndex = items.indexOfFirst { it.first == period }.coerceAtLeast(0)
    TabRow(selectedTabIndex = selectedIndex) {
        items.forEachIndexed { index, (p, label) ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onChange(p) },
                text = { Text(label) }
            )
        }
    }
}

/** 새 스키마에 맞춘 미션 행 */
@Composable
private fun MissionRow(m: MissionEntity) {
    ListItem(
        headlineContent = { Text(m.mission) },
        supportingContent = {
            Column {
                Text("타입 ${m.type}  •  subId ${m.subId}")
                Spacer(Modifier.height(4.dp))
                // 유효 기간
                Text(
                    "유효: ${m.validFrom} ~ ${m.validTo}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                if (m.dsl.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        m.dsl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        trailingContent = {
            // 상태(status/progress)가 스키마에 없으므로 칩 대신 중립 배지
            NeutralChip("추천")
        }
    )
}

/** 중립 칩 */
@Composable
private fun NeutralChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.clip(MaterialTheme.shapes.small)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** 리스트 키: 새 필드 조합으로 고유값 생성 */
private fun MissionEntity.keyForList(): String =
    "${type}-${subId}-${validFrom}-${validTo}"
