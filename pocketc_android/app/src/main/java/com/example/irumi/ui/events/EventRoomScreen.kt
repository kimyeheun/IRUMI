package com.example.irumi.ui.events

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.irumi.core.designsystem.component.dialog.TwoButtonDialog
import com.example.irumi.core.designsystem.component.tooltip.InfoTooltip
import com.example.irumi.data.dto.response.events.RoomStatus
import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.MemberEntity
import com.example.irumi.domain.entity.PuzzleEntity
import com.example.irumi.domain.entity.RankEntity
import com.example.irumi.domain.entity.RoomEntity
import com.example.irumi.ui.payments.PullRefreshContent
import com.example.irumi.ui.payments.TossColors
import com.example.irumi.ui.theme.BrandGreen
import kotlinx.coroutines.delay

// 토스 스타일 컬러 팔레트
object SampleColors {
    val Primary = BrandGreen//Color(0xFF1E5EFF)
    val PrimaryLight = Color(0xFF4D7EFF)
    val Success = Color(0xFF00C896)
    val Error = Color(0xFFFF4545)
    val Warning = Color(0xFFFFA726)
    val Background = Color(0xFFF8F9FA)
    val Surface = Color.White
    val OnSurface = Color(0xFF191F28)
    val OnSurfaceVariant = Color(0xFF6B7684)
    val Outline = Color(0xFFE5E8EB)
    val Gray50 = Color(0xFFF8F9FA)
    val Gray100 = Color(0xFFF1F3F4)
    val Gray200 = Color(0xFFE5E8EB)
    val Gray300 = Color(0xFFCDD2D7)
    val Gray400 = Color(0xFFB0B8C1)
    val Gray500 = Color(0xFF8B95A1)
}
@Composable
fun EventRoomScreen(
    viewModel: EventViewModel = hiltViewModel(),
    roomEntity: RoomEntity,
    eventEntity: EventEntity,
    isSuccess: Boolean? = null,
    isRefresh: Boolean = false,
    onRefresh: () -> Unit,
    onLeaveClick: () -> Unit,
    onFollowClick: (Int) -> Unit,
    onMatchButtonClick: () -> Unit
) {
    // ViewModel 관리 변수
    val context = LocalContext.current
    val puzzleImageUrl by viewModel.puzzleImageUrl.collectAsState()

    // UI 전용 상태
    val placeholderBitmap = rememberPlaceholderBitmap()
    var bitmap by remember { mutableStateOf<Bitmap?>(placeholderBitmap) }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    val userRankings = remember(roomEntity) {
        roomEntity.ranks.map { rankEntity ->
            val member = roomEntity.members.find { it.userId == rankEntity.userId }
            UserRanking(
                userId = rankEntity.userId,
                rank = rankEntity.rank,
                nickname = member?.name ?: "Unknown",
                filledCount = rankEntity.count,
                totalPieces = roomEntity.totalPieces
            )
        }
    }

    LaunchedEffect(puzzleImageUrl) {
        bitmap = loadBitmapFromUrl(context, puzzleImageUrl, placeholderBitmap)
    }

    Scaffold(
        topBar = {
            TopBar(
                context = context,
                isSuccess = isSuccess,
                roomCode = roomEntity.roomCode,
                onLeaveClick = onLeaveClick
            )
        }
    ) { innerPadding ->

        PullRefreshContent(
            isRefreshing = isRefresh,
            modifier = Modifier.fillMaxSize(),
            onRefresh = onRefresh,
            content = {
                LazyColumn(
                    modifier = Modifier
                        .background(SampleColors.Background)
                        .padding(top = innerPadding.calculateTopPadding())
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        PuzzleMembers(
                            maxMembers = roomEntity.maxMembers,
                            members = roomEntity.members,
                            onFollowClick = onFollowClick
                        )
                    }

                    item {
                        if (isSuccess != null) {
                            GameResultSection(
                                isSuccess = isSuccess,
                                badgeImageUrl = eventEntity.badgeImageUrl,
                                badgeName = eventEntity.badgeName
                            )
                        } else {
                            ProgressCard(
                                filledCount = roomEntity.puzzles.size,
                                totalPieces = roomEntity.totalPieces
                            )
                        }
                    }

                    item {
                        bitmap?.let { loadedBitmap ->
                            PuzzleGrid(
                                isSuccess = isSuccess,
                                roomEntity = roomEntity,
                                members = roomEntity.members,
                                selectedUserId = selectedUserId,
                                bitmap = loadedBitmap,
                                onPuzzleClick = { /* TODO */ }
                            )
                        } ?: LoadingPlaceholder()
                    }

                    item {
                        AttemptsSection(
                            attemptsRemaining = roomEntity.puzzleAttempts,
                            isSuccess = isSuccess,
                            onMatchButtonClick = onMatchButtonClick
                        )
                    }

                    items(userRankings) { ranking ->
                        RankingItem(ranking) { userId ->
                            selectedUserId = userId
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        )

    }
}

@Composable
fun TopBar(
    context: Context,
    isSuccess: Boolean?,
    roomCode: String,
    onLeaveClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp),
        colors = CardDefaults.cardColors(containerColor = SampleColors.Surface),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 방코드
            Box(
                modifier = Modifier.weight(0.3f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "방코드",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(
                            TossColors.Primary,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { showDialog = true }
                )
            }

            // 2. 퍼즐 게임
            Box(
                modifier = Modifier.weight(0.4f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "퍼즐 게임",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SampleColors.OnSurface
                )
            }

            // 3. 나가기
            Box(
                modifier = Modifier.weight(0.3f),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (isSuccess == null) {
                    TextButton(
                        onClick = {
                            showLeaveDialog = true
                                  },
                        colors = ButtonDefaults.textButtonColors(contentColor = SampleColors.Error)
                    ) {
                        Text(
                            "나가기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        InviteDialog(
            context = context,
            roomCode = roomCode,
            onDismiss = { showDialog = false } // 닫기
        )
    }

    if (showLeaveDialog) {
        TwoButtonDialog (
            onDismissRequest = {
                showLeaveDialog = false
            },
            title =  "방 나가기" ,
            text =  "방을 나가면 지금까지 친구와 함께 맞춘 퍼즐은 사라져요. 그래도 나가시겠어요?",
            confirmButtonText = "나가기",
            dismissButtonText = "취소",
            onConfirmFollow = {
                onLeaveClick()
                showLeaveDialog = false
            }
        )
    }
}

@Composable
fun InviteDialog(
    context: Context,
    roomCode: String,
    onDismiss: () -> Unit
) {
    TwoButtonDialog (
        onDismissRequest = onDismiss,
        title =  "방코드" ,
        text =  "$roomCode\n방코드를 친구에게 공유하세요!",
        confirmButtonText = "복사",
        dismissButtonText = "취소",
        onConfirmFollow = {
            // 클립보드에 복사
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("방코드", roomCode)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "방코드가 복사됐어요", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    )
}

@Composable
fun ProgressCard(filledCount: Int, totalPieces: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = SampleColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "진행률",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SampleColors.OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$filledCount / $totalPieces",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = SampleColors.Primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            val progress = if (totalPieces > 0) filledCount.toFloat() / totalPieces else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SampleColors.Gray200)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(SampleColors.Primary, SampleColors.PrimaryLight)
                            )
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGame() {
    GameResultSection(
        isSuccess = true,
        badgeImageUrl = "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/badges/event1.png" ,
        badgeName = "dd"
    )
}


@Composable
fun GameResultSection(
    isSuccess: Boolean,
    badgeImageUrl: String,
    badgeName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuccess) BrandGreen else SampleColors.Error
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 12.dp,         // 그림자 강도
                        shape = CircleShape,       // 원형
                        ambientColor = Color.Yellow.copy(alpha = 0.9f),
                        spotColor = Color.Yellow.copy(alpha = 0.9f)
                    )
            ) {
                AsyncImage(
                    model = badgeImageUrl,
                    contentDescription = "뱃지 이미지",
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = badgeName,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isSuccess) "획득 성공!" else "획득 실패",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            if(isSuccess) {
                JumpingText(
                    text = "홈에서 획득한 배지를 확인하세요",
                    fontSize = 16,
                    color = Color.White,
                    targetValue = 5f,
                    durationMillis = 400
                )
            }else {
                Text(
                    text = "다음엔 꼭 성공해보세요",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun JumpingText(
    text: String,
    fontSize: Int,
    color: Color,
    targetValue: Float,
    durationMillis: Long
) {
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            // 위로 점프
            offsetY.animateTo(
                targetValue = -targetValue, // 위로 10px 이동
                animationSpec = tween(150, easing = FastOutLinearInEasing)
            )
            // 아래로 내려오기
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(150, easing = FastOutLinearInEasing)
            )
            delay(durationMillis) // 반복 간격
        }
    }

    Text(
        text = text,
        fontSize = fontSize.sp,
        color = color.copy(alpha = 0.9f),
        modifier = Modifier.offset(y = offsetY.value.dp) // Y축 이동
    )
}


@Composable
fun PuzzleMembers(
    maxMembers: Int,
    members: List<MemberEntity>,
    onFollowClick: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedMemberForDialog by remember { mutableStateOf<MemberEntity?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = SampleColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column (
            modifier = Modifier.padding(20.dp)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "참가자",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SampleColors.OnSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )

                Text(
                    text = "${members.size} / $maxMembers",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SampleColors.Primary,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(members) { index, member ->
                    PuzzleMemberItem(
                        isEmpty = false,
                        index = index,
                        member = member
                    ) {
                        if (!member.isFriend) {
                            selectedMemberForDialog = member
                            showDialog = true
                        }
                    }
                }
            }
        }
    }

    selectedMemberForDialog?.let { memberToShow ->
        if (showDialog) {
            TwoButtonDialog(
                title = "${memberToShow.name}님을 팔로우하시겠어요?",
                text = "팔로우하면 해당 멤버의 활동 소식을 받아볼 수 있습니다.",
                confirmButtonText = "팔로우",
                dismissButtonText = "취소",
                onDismissRequest = {
                    showDialog = false
                    selectedMemberForDialog = null
                },
                onConfirmFollow = {
                    onFollowClick(memberToShow.userId)
                    showDialog = false
                    selectedMemberForDialog = null
                }
            )
        }
    }
}

@Composable
fun PuzzleMemberItem(
    isEmpty: Boolean,
    index: Int?,
    member: MemberEntity?,
    onMemberLongClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(
                if(!isEmpty && index == 0) {
                    Modifier
                }else {
                    Modifier.combinedClickable(
                        onClick = { },
                        onLongClick = {
                            onMemberLongClick()
                        }
                    )
                }
            )

    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isEmpty) Color.LightGray else Color.Transparent)
        ) {
            if (!isEmpty) {
                AsyncImage(
                    model = member?.profileImageUrl,
                    contentDescription = "프로필 이미지",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = member?.name ?: "",
            fontSize = 12.sp,
            color = SampleColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PuzzleGrid(
    isSuccess: Boolean?,
    roomEntity: RoomEntity,
    members: List<MemberEntity>,
    bitmap: Bitmap,
    selectedUserId: Int?,
    onPuzzleClick: (Int) -> Unit
) {
    val totalCols = when(roomEntity.maxMembers) {
        2 -> 5
        3 -> 7
        5 -> 9
        else -> 7
    }
    val totalRows = totalCols
    val puzzleMap = remember(roomEntity.puzzles) {
        roomEntity.puzzles.associateBy { it.row to it.column }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = SampleColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "퍼즐",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SampleColors.OnSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if(isSuccess != null && isSuccess) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "퍼즐 완성 이미지",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }else {
                for (rowIdx in 1..totalRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (colIdx in 1..totalCols) {
                            val puzzleEntity = puzzleMap[rowIdx to colIdx]

                            PuzzleItem(
                                puzzle = puzzleEntity,
                                piece = cropBitmapPiece(
                                    bitmap,
                                    rowIdx,
                                    colIdx,
                                    totalRows,
                                    totalCols
                                ),
                                onPuzzleClick = onPuzzleClick,
                                selectedUserId = selectedUserId,
                                members = members,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PuzzleItem(
    puzzle: PuzzleEntity?,
    piece: ImageBitmap?,
    onPuzzleClick: (Int) -> Unit,
    selectedUserId: Int?,
    members: List<MemberEntity>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (selectedUserId != null && puzzle?.userId == selectedUserId) {
                    Modifier.border(2.dp, SampleColors.Primary, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (puzzle?.userId != null && piece != null) {
            Image(
                painter = BitmapPainter(piece),
                contentDescription = "퍼즐 조각",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        SampleColors.Gray100,
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        SampleColors.Gray200,
                        RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

@Composable
fun AttemptsSection(
    attemptsRemaining: Int,
    isSuccess: Boolean?,
    onMatchButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = SampleColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "퍼즐 조각",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SampleColors.OnSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(2.dp))
                InfoTooltip("퍼즐", "미션을 달성하면 퍼즐 조각을 받을 수 있습니다")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$attemptsRemaining",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SampleColors.Primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onMatchButtonClick,
                enabled = attemptsRemaining > 0 && isSuccess == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SampleColors.Primary,
                    contentColor = Color.White,
                    disabledContainerColor = SampleColors.Gray400, // 비활성화 색상
                    disabledContentColor = Color.White // 비활성화 시 글자색
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            ) {
                Text(
                    "맞추기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RankingItem(
    ranking: UserRanking,
    onUserClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onUserClick(ranking.userId) },
        colors = CardDefaults.cardColors(containerColor = SampleColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when (ranking.rank) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> SampleColors.Gray300
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${ranking.rank}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (ranking.rank <= 3) Color.White else SampleColors.OnSurface
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = ranking.nickname,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = SampleColors.OnSurface
                )
            }

            Text(
                text = "${ranking.filledCount} / ${ranking.totalPieces}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SampleColors.Primary
            )
        }
    }
}

@Composable
fun LoadingPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = SampleColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SampleColors.Gray200)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "퍼즐 이미지 로딩 중...",
                    fontSize = 14.sp,
                    color = SampleColors.OnSurfaceVariant
                )
            }
        }
    }
}

data class UserRanking(
    val userId: Int,
    val rank: Int,
    val nickname: String,
    val filledCount: Int,
    val totalPieces: Int
)

fun rememberPlaceholderBitmap(): Bitmap {
    return createBitmap(100, 100).apply {
        eraseColor(android.graphics.Color.GRAY)
    }
}

suspend fun loadBitmapFromUrl(context: Context, url: String?, placeholder: Bitmap): Bitmap {
    if (url.isNullOrEmpty()) return placeholder

    return try {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        val drawable = (result as? SuccessResult)?.drawable
        if (drawable is BitmapDrawable) drawable.bitmap else placeholder
    } catch (e: Exception) {
        placeholder
    }
}

fun cropBitmapPiece(
    bitmap: Bitmap,
    row: Int,
    col: Int,
    totalRows: Int,
    totalCols: Int
): ImageBitmap {
    val pieceWidth = bitmap.width / totalCols
    val pieceHeight = bitmap.height / totalRows
    val x = (col - 1) * pieceWidth
    val y = (row - 1) * pieceHeight
    return Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceHeight).asImageBitmap()
}

@Preview(showBackground = true)
@Composable
fun PreviewEventRoomScreen() {
    val dummyRoomEntity = RoomEntity(
        roomId = 1,
        createdAt = "2025-09-21T10:00:00Z",
        maxMembers = 4,
        puzzleAttempts = 5,
        status = RoomStatus.IN_PROGRESS,
        roomCode = "ABCD123",
        puzzles = List(16) { index ->
            PuzzleEntity(
                pieceId = index + 1,
                row = (index / 4) + 1,
                column = (index % 4) + 1,
                userId = if (index % 3 == 0) 1 else 0
            )
        },
        ranks = listOf(
            RankEntity(userId = 1, rank = 1, count = 4),
            RankEntity(userId = 2, rank = 2, count = 2)
        ),
        members = listOf(
            MemberEntity(userId = 1, name = "UserA", profileImageUrl = "", isFriend = true),
            MemberEntity(userId = 2, name = "UserB", profileImageUrl = "", isFriend = false)
        ),
        totalPieces = 25
    )
    val dummyEventEntity = EventEntity(
        eventId = 101,
        eventName = "Dummy Event",
        eventDescription = "This is a dummy event for preview.",
        eventImageUrl = "https://example.com/dummy_event.png",
        badgeName = "Dummy Badge",
        badgeDescription = "Dummy Badge Description",
        startAt = "2025-09-01T00:00:00Z",
        endAt = "2025-09-30T23:59:59Z",
        badgeImageUrl = ""
    )

    EventRoomScreen(roomEntity = dummyRoomEntity, eventEntity = dummyEventEntity, isSuccess = true, onRefresh = {}, onLeaveClick = {}, onFollowClick = {}, onMatchButtonClick = {}) // Example for preview
}
