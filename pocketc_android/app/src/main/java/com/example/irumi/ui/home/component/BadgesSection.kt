package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import com.example.irumi.domain.entity.main.BadgeEntity
import com.example.irumi.ui.theme.BrandGreen
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BadgesSection(
    badges: List<BadgeEntity>,
    title: String = "나의 뱃지",
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: String? = null,
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF191F28),
            letterSpacing = (-0.5).sp,
            lineHeight = 28.sp
        )
        Spacer(Modifier.height(10.dp))

        // 1) 에러
        if (error != null) {
            ErrorBox(message = error, onRetry = onRetry)
            return@Column
        }

        // 2) 로딩
        if (isLoading) {
            LoadingGridSkeleton()
            return@Column
        }

        // 3) 빈 상태
        if (badges.isEmpty()) {
            EmptyBox()
            return@Column
        }

        // 4) 콘텐츠 (가로 스크롤)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(badges) { badge ->
                BadgeItem(
                    badge = badge,
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingGridSkeleton() {
    // 3x2 정도의 스켈레톤
    Column {
        repeat(2) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(108.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF7F8FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun EmptyBox() {
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
}

@Composable
private fun ErrorBox(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF1F2))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("뱃지를 불러오지 못했어요", color = Color(0xFFB91C1C), fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(message, color = Color(0xFFB91C1C), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
        ) { Text("다시 시도") }
    }
}

@Composable
private fun BadgeItem(
    badge: BadgeEntity,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F8FA))
            .padding(10.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(badge.badgeImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = badge.badgeName,
            contentScale = ContentScale.Crop,
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
