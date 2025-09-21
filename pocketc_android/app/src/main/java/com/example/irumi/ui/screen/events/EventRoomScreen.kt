package com.example.irumi.ui.screen.events

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.MemberEntity
import com.example.irumi.domain.entity.PuzzleEntity
import com.example.irumi.domain.entity.RankEntity
import com.example.irumi.domain.entity.RoomEntity

@Composable
fun EventRoomScreen(viewModel: EventViewModel = hiltViewModel(), roomEntity: RoomEntity, eventEntity: EventEntity, isSuccess: Boolean? = null) {
    val context = LocalContext.current

    val puzzleImageUrl by viewModel.puzzleImageUrl.collectAsState()

    val placeholderBitmap = rememberPlaceholderBitmap()

    // TODO 뷰모델로?
    var bitmap by remember { mutableStateOf<Bitmap?>(placeholderBitmap) }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(puzzleImageUrl) {
        bitmap = loadBitmapFromUrl(context, puzzleImageUrl, placeholderBitmap)
    }

    Scaffold(
        topBar = {
            TopBar(
                filledCount = roomEntity.puzzles.size,
                totalPieces = roomEntity.puzzles.size // TODO 계산 해야 함.
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            isSuccess?.let { success ->
                item {
                    GameResultSection(success)
                }
            }
            item {
                PuzzleMembers(roomEntity.members)
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {

                bitmap?.let { loadedBitmap ->
                    PuzzleGrid(
                        roomEntity = roomEntity,
                        members = roomEntity.members,
                        selectedUserId = selectedUserId,
                        bitmap = loadedBitmap,
                        onPuzzleClick = {
                            // TODO: 퍼즐 클릭 시 로직 구현
                            // 예를 들어, viewModel.onPuzzleClick(it) 호출
                            //selectedUserId = it
                        }
                    )
                } ?: LoadingPlaceholder()
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                AttemptsSection(
                    attemptsRemaining = roomEntity.puzzleAttempts,
                    onMatchButtonClick = {
                        // TODO: '맞추기' 버튼 클릭 시 로직 구현
                        // 예를 들어, viewModel.onMatchButtonClick() 호출
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(
                items = roomEntity.ranks.map { rankEntity ->
                    val member = roomEntity.members.find { it.userId == rankEntity.userId }
                    UserRanking(
                        userId = rankEntity.userId, // Pass userId
                        rank = rankEntity.rank,
                        nickname = member?.name ?: "Unknown",
                        filledCount = rankEntity.count,
                        totalPieces = roomEntity.puzzles.size
                    )
                }
            ) { ranking ->
                RankingItem(ranking) { userId -> selectedUserId = userId } // Pass the lambda
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PuzzleMembers(members: List<MemberEntity>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(members) { member ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { // TODO 멤버 클릭 시 팔로우 ~
                         }
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Text(member.name, fontSize = 14.sp)
            }
        }
//        item {
//            Box(
//                modifier = Modifier
//                    .size(60.dp)
//                    .clip(CircleShape)
//                    .background(Color.Gray),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("+", fontSize = 24.sp, color = Color.White)
//            }
//        }
    }
}

@Composable
fun PuzzleGrid(roomEntity: RoomEntity, members: List<MemberEntity>, bitmap: Bitmap, selectedUserId: Int?, onPuzzleClick: (Int) -> Unit) {
    val totalCols = when(roomEntity.maxMembers) {
        2 -> 5
        3 -> 7
        5 -> 9
        else -> 9
    }
    val totalRows = totalCols
    val puzzleMap = remember(roomEntity.puzzles) {
        roomEntity.puzzles.associateBy { it.row to it.column }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                        onPuzzleClick = onPuzzleClick, // Pass the click handler
                        selectedUserId = selectedUserId, // TODO Pass selectedUserId
                        members = members, // TODO ? Pass members for PuzzleItem to resolve filledBy
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(filledCount: Int, totalPieces: Int) {
    Box (
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$filledCount / $totalPieces",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
        Button(
            onClick = { /* 나가기 버튼 클릭 이벤트 */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text("나가기", color = Color.White)
        }
    }
}

// 게임 성공/실패 텍스트를 보여주는 Composable
@Composable
fun GameResultSection(isSuccess: Boolean) {
    Text(
        text = if (isSuccess) "성공" else "실패",
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSuccess) Color(0xFF4CAF50) else Color.Red,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun AttemptsSection(attemptsRemaining: Int, onMatchButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "남은 횟수: $attemptsRemaining",
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onMatchButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("맞추기", color = Color.White)
        }
    }
}

@Composable
fun RankingItem(ranking: UserRanking, onUserClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(16.dp)
            .clickable { onUserClick(ranking.userId) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${ranking.rank}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = ranking.nickname, fontSize = 16.sp)
        }
        Text(
            text = "${ranking.filledCount} / ${ranking.totalPieces}",
            fontSize = 16.sp
        )
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

@Composable
fun LoadingPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("퍼즐 이미지 로딩 중...")
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
            .then(
                if (selectedUserId != null && puzzle?.userId == selectedUserId) {
                    Modifier.border(2.dp, Color.Blue, RoundedCornerShape(4.dp))
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
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            )
        }
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
        status = "IN_PROGRESS",
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
        )
    )
    val dummyEventEntity = EventEntity(
        eventId = 101,
        eventName = "Dummy Event",
        eventDescription = "This is a dummy event for preview.",
        eventImageUrl = "https://example.com/dummy_event.png",
        badgeName = "Dummy Badge",
        badgeDescription = "Dummy Badge Description",
        startAt = "2025-09-01T00:00:00Z",
        endAt = "2025-09-30T23:59:59Z"
    )

    EventRoomScreen(roomEntity = dummyRoomEntity, eventEntity = dummyEventEntity, isSuccess = true) // Example for preview
}
