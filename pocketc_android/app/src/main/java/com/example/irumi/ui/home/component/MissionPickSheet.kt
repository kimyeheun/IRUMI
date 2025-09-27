package com.example.irumi.ui.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.domain.entity.main.MissionEntity
import com.example.irumi.ui.theme.BrandGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionPickSheet(
    missions: List<MissionEntity>,
    onDismiss: () -> Unit,
    onConfirm: (List<Int>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedMissions by remember(missions) {
        mutableStateOf(emptySet<Int>())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE5E8EB))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // 헤더
            Column {
                Text(
                    text = "오늘의 추천 미션",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191F28)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "AI가 고른 최대 5개 미션 중 원하는 것만 선택하세요.\n직접 선택하지 않으면 모든 미션이 자동으로 선택돼요!",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF6B7684)
                )
            }

            Spacer(Modifier.height(28.dp))

            // 전체 선택/해제 버튼
            OutlinedButton(
                onClick = {
                    selectedMissions = if (selectedMissions.size == missions.size) {
                        emptySet()
                    } else {
                        missions.map { it.missionId }.toSet()
                    }
                },
                modifier = Modifier
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF4E5968)
                ),
                border = BorderStroke(1.dp, Color(0xFFE5E8EB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (selectedMissions.size == missions.size) "전체 해제" else "전체 선택",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))

            // 미션 리스트
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(missions, key = { it.missionId }) { mission ->
                    TossMissionItem(
                        mission = mission,
                        isSelected = mission.missionId in selectedMissions,
                        onSelectionChange = { isSelected ->
                            selectedMissions = if (isSelected) {
                                selectedMissions + mission.missionId
                            } else {
                                selectedMissions - mission.missionId
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // 버튼 영역
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 메인 버튼
                Button(
                    onClick = { onConfirm(selectedMissions.toList()) },
                    enabled = selectedMissions.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        disabledContainerColor = Color(0xFFE5E8EB)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (selectedMissions.isEmpty()) "미션을 선택해주세요"
                        else "${selectedMissions.size}개 미션 선택 완료",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedMissions.isNotEmpty()) Color.White
                        else Color(0xFF8B95A1)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TossMissionItem(
    mission: MissionEntity,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource
            ) {
                onSelectionChange(!isSelected)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BrandGreen.copy(alpha = 0.06f) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) BrandGreen else Color(0xFFE5E8EB)
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mission.mission,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF191F28),
                    lineHeight = 22.sp
                )
            }

            Spacer(Modifier.width(16.dp))

            // 커스텀 체크박스
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) BrandGreen else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = if (isSelected) BrandGreen else Color(0xFFD1D6DB),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreTemp() {
    Surface(color = Color(0xFFF7F8FA)) {
        Column {
            MissionPickSheet(
                missions = listOf(
                    MissionEntity(1, 4, 0, "오늘 <간식> 결제 1번만 하기", "IN_PROGRESS", 0, 0, ""),
                    MissionEntity(2, 4, 0, "오늘 <커피> 결제 2번까지만 하기", "IN_PROGRESS", 0, 0, ""),
                    MissionEntity(3, 4, 0, "점심시간에 산책 10분 이상 하기", "IN_PROGRESS", 0, 0, "")
                ),
                onDismiss = { },
                onConfirm = { }
            )
        }
    }
}