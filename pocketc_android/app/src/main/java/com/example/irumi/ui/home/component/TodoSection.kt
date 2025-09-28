package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.core.designsystem.component.tooltip.InfoTooltip
import com.example.irumi.domain.entity.main.MissionEntity
import com.example.irumi.ui.events.SampleColors
import com.example.irumi.ui.home.model.TodoItemUi
import com.example.irumi.ui.theme.BrandGreen
import java.text.NumberFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** 내부 표시용 상태 */
enum class UiStatus { ACTIVE, SUCCESS, FAILED }

// ★ 상태 칩
@Composable
private fun StatusChip(
    status: UiStatus,
    modifier: Modifier? = Modifier
) {
    val (bg, fg, label) = when (status) {
        UiStatus.SUCCESS -> Triple(
            BrandGreen.copy(alpha = 0.18f),
            BrandGreen,
            "성공"
        )
        UiStatus.FAILED -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "실패"
        )
        UiStatus.ACTIVE -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "진행중"
        )
    }
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(bg)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TodoSection(
    missionReceived: Boolean,
    missions: List<MissionEntity>,
    isUpdatingId: Int? = null,
    error: String? = null,
    onToggle: (id: Int, newValue: Boolean) -> Unit = { _, _ -> }, // 호출부 호환용(미사용)
    modifier: Modifier = Modifier
) {
    // 상단 토글: 일/주/월
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("일간", "주간", "월간")

    // 서버 모델 → UI 모델 매핑 (★ 상태 판정: status 우선, 다음 progress)
    val uiItems = remember(missions) {
        missions.map {
            val success = it.status.equals("SUCCESS", true) ||
                    it.status.equals("DONE", true) ||
                    (it.progress ?: 0) >= (it.value ?: 100)
            val failed = it.status.equals("FAILED", true)

            val uiStatus = when {
                success -> UiStatus.SUCCESS
                failed -> UiStatus.FAILED
                else -> UiStatus.ACTIVE
            }
            val dim = if (uiStatus != UiStatus.ACTIVE) 0.75f else 1f

            TodoItemUi(
                id = it.missionId,
                title = it.mission,
                status = uiStatus,
                dimAlpha = dim,
                type = it.type,
                template = it.template ?: "",
                progress = it.progress ?: 0,
                value = it.value ?: 100
            )
        }
    }

    // type으로 기간 분류 (0: Daily, 1: Weekly, 2: Monthly)
    val (daily, weekly, monthly) = remember(uiItems) {
        val day = uiItems.filter { it.type == 0 }
        val week = uiItems.filter { it.type == 1 }
        val month = uiItems.filter { it.type == 2 }
        Triple(day, week, month)
    }

    val currentItems = when (selectedTab) {
        1 -> weekly
        2 -> monthly
        else -> daily
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("AI 추천 미션", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(2.dp))
            InfoTooltip(title="미션", description = "소비 내역 맞춤형 AI미션입니다", iconSize=14)
            Spacer(Modifier.weight(1f))
            Text(
                text = "매일 오전 12시 미션 생성",
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp, color = Color.LightGray
            )
        }
        Spacer(Modifier.height(8.dp))

        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(6.dp))
        }

        // 탭 (배경 투명 + BrandGreen)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = BrandGreen,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = BrandGreen,
                    height = 2.dp
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(label) },
                    selectedContentColor = BrandGreen,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        if (!missionReceived || uiItems.isEmpty()) {
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

        // 리스트
        if (currentItems.isEmpty()) {
            Text(
                text = when (selectedTab) {
                    1 -> "주간 미션이 없어요."
                    2 -> "월간 미션이 없어요."
                    else -> "일간 미션이 없어요."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SampleColors.Gray50)
                            .padding(16.dp)
                            .alpha(item.dimAlpha),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top // CenterVertically -> Top으로 변경
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp, // 줄 높이 명시
                                    modifier = Modifier.weight(0.7f) // 70% 비율 보장
                                )

                                Spacer(Modifier.width(8.dp)) // 여백 늘림

                                StatusChip(
                                    item.status,
                                    modifier = Modifier.weight(0.3f, fill = false) // 30% 최대, 내용만큼만 차지
                                )
                            }

                            Spacer(Modifier.height(4.dp)) // 여백 늘림

                            // 진행도는 아래쪽에
                            if (!item.template.contains("BAN")) {
                                AmountProgressIndicator(
                                    isCount = item.template.startsWith("COUNT"),
                                    current = item.progress,
                                    total = item.value,
                                    status = item.status
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ★ 금액 달성도
@Composable
private fun AmountProgressIndicator(
    isCount: Boolean,
    current: Int,
    total: Int,
    status: UiStatus,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getNumberInstance(Locale.KOREA)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${formatter.format(current)}" + if (isCount) "회" else "원",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = when (status) {
                    UiStatus.SUCCESS -> BrandGreen
                    UiStatus.FAILED -> MaterialTheme.colorScheme.error
                    else -> Color(0xFF6B7684)
                }
            )
            Text(
                text = " / ${formatter.format(total)}" + if (isCount) "회" else "원",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = when (status) {
                    UiStatus.SUCCESS -> BrandGreen
                    UiStatus.FAILED -> MaterialTheme.colorScheme.error
                    else -> Color(0xFF6B7684)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreTodoSection2() {
    TodoSection(
        missionReceived = true,
        missions = listOf(
            MissionEntity(
                missionId = 1,
                subId = 2,
                type = 0, // Daily
                mission = "오늘 간식 ㅁ널이힝라힝랗 10000원 이하로 쓰기",
                status = "ACTIVE",
                progress = 7500,
                value = 10000,
                template = "SPEND_CAP_DAILY"
            ),
            MissionEntity(
                missionId = 2,
                subId = 2,
                type = 1, // Weekly
                mission = "이번주 커피 5번 이하로 마시기",
                status = "SUCCESS",
                progress = 3,
                value = 5,
                template = "COUNT_CAP_WEEKLY"
            ),
            MissionEntity(
                missionId = 3,
                subId = 3,
                type = 2, // Monthly
                mission = "한 달 배달음식 20만원 이하로 쓰기",
                status = "ACTIVE",
                progress = 150000,
                value = 200000,
                template = "SPEND_CAP_MONTHLY"
            ),
            MissionEntity(
                missionId = 4,
                subId = 4,
                type = 0, // Daily
                mission = "오늘 운동 지출하지 않기",
                status = "FAILED",
                progress = 0,
                value = 1,
                template = "CATEGORY_BAN_DAILY"
            )
        ),
        isUpdatingId = null,
        error = null,
        onToggle = { _, _ -> },
        modifier = Modifier.padding(10.dp)
    )
}

/** ===== 기존 유틸 (그대로 유지) ===== */
private enum class MissionTimeStatus { UPCOMING, ACTIVE, EXPIRED }

private fun computeTimeStatus(validFrom: String, validTo: String): MissionTimeStatus {
    val now = Instant.now().truncatedTo(ChronoUnit.SECONDS)
    val from = parseIsoInstant(validFrom) ?: return MissionTimeStatus.ACTIVE
    val to = parseIsoInstant(validTo) ?: return MissionTimeStatus.ACTIVE
    return when {
        now.isBefore(from) -> MissionTimeStatus.UPCOMING
        now.isAfter(to) -> MissionTimeStatus.EXPIRED
        else -> MissionTimeStatus.ACTIVE
    }
}

private fun parseIsoInstant(s: String): Instant? {
    return try {
        OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
    } catch (_: Throwable) {
        try {
            Instant.parse(s)
        } catch (_: Throwable) {
            null
        }
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
