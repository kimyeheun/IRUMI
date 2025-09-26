package com.example.irumi.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.irumi.data.dto.request.auth.AuthEditRequest
import com.example.irumi.domain.entity.main.UserProfileEntity
import com.example.irumi.ui.auth.AuthViewModel
import com.example.irumi.ui.events.SampleColors
import com.example.irumi.ui.home.HomeViewModel
import com.example.irumi.ui.payments.TossColors
import com.example.irumi.ui.theme.BrandGreen
import java.text.DecimalFormat

import com.example.irumi.ui.theme.LightGray   // ★ 추가

@Composable
fun MyPageScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    onLoggedOut: () -> Unit, // 인트로 화면으로 이동
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val money = remember { DecimalFormat("#,##0원") }

    val error = authViewModel.error
    val isLoggedIn = authViewModel.isLoggedIn

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            authViewModel.updateMe(
                AuthEditRequest(
                    uiState.profile?.name!!,
                    uiState.profile?.budget!!
                )
            )
        }
    }

    // 로그아웃 성공 감지
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) onLoggedOut()
    }

    // 에러 토스트
    val ctx = LocalContext.current
    LaunchedEffect(error) {
        error?.let { Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show() }
    }

    // ★ 전체 배경 LightGray 적용
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightGray
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp) // 여백도 줄 수 있음
        ) {
            item {
                ProfileHeaderCard(
                    profile = uiState.profile,
                    money = money,
                    onLoggedOut = {
                        imagePickerLauncher.launch("image/*")
                    }
                )
            }

            item {
                ActivityStatsCard(
                    followCount = uiState.followInfos.size,
                    badgeCount = uiState.badges.size,
                    streakCount = uiState.streaks.size
                )
            }

            item {
                SettingsSection(
                    onLoggedOut = {
                        authViewModel.logout()
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    profile: UserProfileEntity?,
    money: DecimalFormat,
    onLoggedOut: () -> Unit
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.height(16.dp))

            // 사용자 이름
            Text(
                text = profile?.name ?: "사용자",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191F28)
            )

            Spacer(modifier = Modifier.height(8.dp))

            MyCodeRow(profile?.userId ?: 0)

            Spacer(modifier = Modifier.height(20.dp))

            // 예산 정보를 리스트 형태로
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 이번 달 예산
                InfoListItem(
                    icon = "💰",
                    title = "이번 달 예산",
                    value = money.format(profile?.budget ?: 0),
                    valueColor = BrandGreen
                )
            }
        }
    }
}

@Composable
fun MyCodeRow(
    userId: Int,
    context: Context = LocalContext.current
) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "@$userId",
            fontSize = 16.sp,
            color = SampleColors.Gray400
        )

        Spacer(Modifier.width(4.dp))

        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "코드 복사",
            tint = TossColors.OnSurfaceVariant,
            modifier = Modifier
                .size(28.dp) // 원하는 크기
                .clip(RoundedCornerShape(4.dp))
                .clickable {
                    val clip = ClipData.newPlainText("내 코드", userId.toString())
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "내 코드가 복사됐어요", Toast.LENGTH_SHORT).show()
                }
                .padding(4.dp) // 아이콘 여백
        )
    }
}

@Composable
private fun InfoListItem(
    icon: String,
    title: String,
    value: String,
    valueColor: Color = Color(0xFF191F28)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F9FA))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF191F28)
            )
        }

        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun ActivityStatsCard(
    followCount: Int,
    badgeCount: Int,
    streakCount: Int
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
                    label = "연속스트릭",
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
private fun SettingsSection(onLoggedOut: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }
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

//            SettingItem(
//                icon = Icons.Default.Edit,
//                title = "프로필 편집",
//                subtitle = "이름, 이메일, 프로필 사진 변경",
//                onClick = { /* TODO */ }
//            )
//
//            SettingItem(
//                icon = Icons.Default.AccountBalanceWallet,
//                title = "예산 설정",
//                subtitle = "월 예산 금액 변경",
//                onClick = { /* TODO */ }
//            )

            SettingItem(
                icon = Icons.Default.ExitToApp,
                title = "로그아웃",
                subtitle = "",
                onClick = { showLogoutDialog = true },
                textColor = Color(0xFFE53E3E)
            )

            // 로그아웃 확인 다이얼로그
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = {
                        Text("로그아웃")
                    },
                    text = {
                        Text("정말 로그아웃 하시겠습니까?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false
                                onLoggedOut()
                            }
                        ) {
                            Text(
                                "로그아웃",
                                color = Color(0xFFE53E3E)
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showLogoutDialog = false }
                        ) {
                            Text("취소")
                        }
                    }
                )
            }
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
