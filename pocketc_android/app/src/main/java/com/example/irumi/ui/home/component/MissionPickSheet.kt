package com.example.irumi.ui.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.irumi.domain.entity.main.MissionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionPickSheet(
    missions: List<MissionEntity>,
    isProcessing: Boolean,
    initiallySelected: Set<Int> = emptySet(),
    onDismiss: () -> Unit,
    onConfirm: (List<Int>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selected by remember(missions) { mutableStateOf(initiallySelected.toMutableSet()) }

    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        sheetState = sheetState
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("오늘의 추천 미션", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("AI가 고른 최대 5개 미션 중 원하는 것만 선택하세요.")

            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(missions, key = { it.missionId }) { m ->
                    ListItem(
                        headlineContent = { Text(m.mission) },
                        supportingContent = { Text("진행도 ${m.progress}% / 타입 ${m.type} / subId ${m.subId}") },
                        trailingContent = {
                            Checkbox(
                                checked = m.missionId in selected,
                                onCheckedChange = { checked ->
                                    if (checked) selected.add(m.missionId) else selected.remove(m.missionId)
                                },
                                enabled = !isProcessing
                            )
                        }
                    )
                    Divider()
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { if (!isProcessing) onDismiss() },
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f)
                ) { Text("나중에") }

                Button(
                    onClick = { if (!isProcessing) onConfirm(selected.toList()) },
                    enabled = selected.isNotEmpty() && !isProcessing,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("선택 완료")
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
