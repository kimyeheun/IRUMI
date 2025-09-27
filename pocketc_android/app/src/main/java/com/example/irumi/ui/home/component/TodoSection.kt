package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.core.designsystem.component.tooltip.InfoTooltip
import com.example.irumi.domain.entity.main.MissionEntity
import com.example.irumi.ui.theme.BrandGreen
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** 내부 표시용 상태 */
enum class UiStatus { ACTIVE, SUCCESS, FAILED }

// ★ 상태 칩
@Composable
private fun StatusChip(status: UiStatus) {
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

/** UI용 간단한 미션 아이템 모델. */
data class TodoItemUi(
    val id: Int,
    val title: String,
    val status: UiStatus, // ★ 진행중/성공/실패
    val dimAlpha: Float   // 성공/실패는 살짝 연하게
)

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
                    (it.progress ?: 0) >= 100
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
                dimAlpha = dim
            )
        }
    }

    // 제목 접두어로 기간 분류
    val (daily, weekly, monthly) = remember(uiItems) {
        val week = mutableListOf<TodoItemUi>()
        val month = mutableListOf<TodoItemUi>()
        val day = mutableListOf<TodoItemUi>()
        uiItems.forEach { item ->
            val t = item.title.trim()
            when {
                t.startsWith("주간") -> week += item
                t.startsWith("한 달") || t.startsWith("한달") -> month += item
                else -> day += item
            }
        }
        Triple(day.toList(), week.toList(), month.toList())
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
            Text("오늘의 미션", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF7F8FA))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .alpha(item.dimAlpha),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 상태 칩 (명확하게)
                        StatusChip(item.status)
                        Spacer(Modifier.width(10.dp))

                        Column(Modifier.weight(1f)) {
                            Text(
                                item.title,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
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
