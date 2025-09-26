// ui/home/component/TodoSection.kt
package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun TodoSection(
    missionReceived: Boolean,
    missions: List<MissionEntity>,
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

        // 아직 추천 미션을 확정하지 않았거나 데이터가 없을 때
        if (!missionReceived || missions.isEmpty()) {
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            missions.forEach { m ->
                val status = remember(m.validFrom, m.validTo) { computeTimeStatus(m.validFrom, m.validTo) }
                val (chipBg, chipFg, chipLabel, textColor) = when (status) {
                    MissionTimeStatus.UPCOMING -> Quad(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        "대기",
                        MaterialTheme.colorScheme.onSurface
                    )
                    MissionTimeStatus.ACTIVE -> Quad(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        "진행중",
                        MaterialTheme.colorScheme.onSurface
                    )
                    MissionTimeStatus.EXPIRED -> Quad(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        "실패",
                        Color(0xFF9CA3AF) // 연회색 텍스트
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7F8FA))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 상태 칩
                    Surface(color = chipBg, contentColor = chipFg, shape = MaterialTheme.shapes.small) {
                        Text(
                            chipLabel,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(m.mission, fontSize = 15.sp, color = textColor)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "타입 ${m.type} • subId ${m.subId}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "유효: ${m.validFrom} ~ ${m.validTo}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        if (m.dsl.isNotBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                m.dsl,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 기간 기반 상태 */
private enum class MissionTimeStatus { UPCOMING, ACTIVE, EXPIRED }

/** ISO 문자열(OffsetDateTime/Instant 호환)로 상태 계산 */
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

/** 다양한 ISO 포맷 방어적 파싱 */
private fun parseIsoInstant(s: String): Instant? {
    return try {
        // 1) OffsetDateTime("2025-09-26T03:53:35.421Z" or +09:00)
        OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
    } catch (_: Throwable) {
        try {
            // 2) Instant.parse("...Z")
            Instant.parse(s)
        } catch (_: Throwable) {
            null
        }
    }
}

/** 간단 튜플 */
private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
