package com.example.irumi.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.irumi.domain.entity.main.DailySavingEntity
import com.example.irumi.domain.entity.main.MissionEntity
import com.example.irumi.domain.entity.main.UserProfileEntity
import com.example.irumi.ui.home.HomeViewModel
import java.text.DecimalFormat

@Composable
fun MyProfile(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val money = remember { DecimalFormat("#,##0원") }
    val BrandGreen = Color(0xFF4CAF93)
    // 프로필 헤더 카드
    ProfileHeaderCard(
        profile = uiState.profile,
        myScore = uiState.myScore,
        money = money,
        BrandGreen = BrandGreen
    )

    // 활동 통계 카드
    ActivityStatsCard(
        followCount = uiState.followInfos.size,
        badgeCount = uiState.badges.size,
        streakCount = uiState.streaks.size,
        BrandGreen = BrandGreen
    )

    // 설정 메뉴
    SettingsSection()
}

@Composable
private fun ProfileHeaderCard(
    profile: UserProfileEntity?,
    myScore: DailySavingEntity?,
    money: DecimalFormat,
    BrandGreen: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 프로필 이미지
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(BrandGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (profile?.profileImageUrl?.isNotEmpty() == true) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(profile.profileImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "프로필 이미지",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = BrandGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // 사용자 정보
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile?.name ?: "사용자",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191F28)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 오늘의 절약 점수와 예산
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreItem(
                    title = "오늘 절약점수",
                    value = "${myScore?.savingScore ?: 0}점",
                    icon = "🎯",
                    color = BrandGreen
                )

                ScoreItem(
                    title = "이번 달 예산",
                    value = money.format(profile?.budget ?: 0),
                    icon = "💰",
                    color = Color(0xFF3B82F6)
                )
            }
        }
    }
}

@Composable
private fun ScoreItem(
    title: String,
    value: String,
    icon: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.08f),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF8B95A1),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ActivityStatsCard(
    followCount: Int,
    badgeCount: Int,
    streakCount: Int,
    BrandGreen: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "활동 현황",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191F28),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "팔로우",
                    value = followCount.toString(),
                    icon = Icons.Default.People,
                    color = BrandGreen
                )

                StatItem(
                    label = "뱃지",
                    value = badgeCount.toString(),
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFFFF9800)
                )

                StatItem(
                    label = "연속일",
                    value = streakCount.toString(),
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFE91E63)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191F28),
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF8B95A1),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun SettingsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "설정",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191F28),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingItem(
                icon = Icons.Default.Edit,
                title = "프로필 편집",
                subtitle = "이름, 이메일, 프로필 사진 변경",
                onClick = { /* TODO */ }
            )

            SettingItem(
                icon = Icons.Default.AccountBalanceWallet,
                title = "예산 설정",
                subtitle = "월 예산 금액 변경",
                onClick = { /* TODO */ }
            )

            SettingItem(
                icon = Icons.Default.Notifications,
                title = "알림 설정",
                subtitle = "미션, 절약 알림 관리",
                onClick = { /* TODO */ }
            )

            SettingItem(
                icon = Icons.Default.ExitToApp,
                title = "로그아웃",
                subtitle = "",
                onClick = { /* TODO */ },
                textColor = Color(0xFFE53E3E)
            )
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color = Color(0xFF191F28)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )

            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF8B95A1),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF8B95A1),
            modifier = Modifier.size(20.dp)
        )
    }
}