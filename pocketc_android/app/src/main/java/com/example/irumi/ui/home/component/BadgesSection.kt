// ui/home/component/BadgesSection.kt
package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.irumi.domain.entity.main.BadgeEntity
import com.example.irumi.ui.theme.BrandGreen
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BadgesSection(
    badges: List<BadgeEntity>,
    title: String = "내 뱃지",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
        Spacer(Modifier.height(10.dp))

        if (badges.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7F8FA)),
                contentAlignment = Alignment.Center
            ) {
                Text("아직 획득한 뱃지가 없어요", color = Color(0xFF6B7280))
            }
            return@Column
        }

        // 🔄 LazyVerticalGrid 대신 FlowRow 사용 (세로 스크롤 생성 X)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            badges.forEach { badge ->
                BadgeItem(
                    badge = badge,
                    modifier = Modifier
                        .width(100.dp)          // 카드 폭 고정(3열 느낌)
                )
            }
        }
    }
}

@Composable
private fun BadgeItem(
    badge: BadgeEntity,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F8FA))
            .padding(10.dp)
    ) {
        AsyncImage(
            model = badge.badgeImageUrl,
            contentDescription = badge.badgeName,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            badge.badgeName,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "Lv.${badge.level}",
            fontSize = 11.sp,
            color = Color(0xFF16A34A),
            fontWeight = FontWeight.Bold
        )
    }
}
