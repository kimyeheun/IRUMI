package com.example.irumi.ui.screen.events.event

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.irumi.model.event.Puzzle
import com.example.irumi.model.event.PuzzleData
import com.example.irumi.model.event.User

@Composable
fun PuzzleScreen(viewModel: PuzzleViewModel = viewModel()) {
    val context = LocalContext.current

    val puzzleDataState by viewModel.puzzleData.collectAsState()
    val puzzleImageUrl by viewModel.puzzleImageUrl.collectAsState()

    val placeholderBitmap = rememberPlaceholderBitmap()

    var bitmap by remember { mutableStateOf<Bitmap?>(placeholderBitmap) }

    LaunchedEffect(puzzleImageUrl) {
        bitmap = loadBitmapFromUrl(context, puzzleImageUrl, placeholderBitmap)
    }

    Scaffold(
        topBar = {
            // todo -> nullable
            TopBar(puzzleDataState?.filledCount ?: 0, puzzleDataState?.totalPieces ?: 0)
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // 퍼즐 부분만 별도의 컴포넌트로 분리
                bitmap?.let { loadedBitmap ->
                    puzzleDataState?.let { data ->
                        // 데이터가 로드된 경우 퍼즐 그리드 표시
                        PuzzleGrid(data, loadedBitmap)
                    } ?: LoadingPlaceholder()
                } ?: LoadingPlaceholder()
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // 횟수 표시
                AttemptsSection(attemptCount = 2) // 예시로 2를 사용

                Spacer(modifier = Modifier.height(24.dp))
            }

            // todo; 맞추기 버튼

            // 랭킹 리스트 부분
            items(
                items = listOf(
                    UserRanking(1, "맹구", 4, 16),
                    UserRanking(2, "짱구", 2, 16),
                    UserRanking(2, "철수", 2, 16),
                    UserRanking(3, "훈이", 1, 16),
                    UserRanking(3, "유리", 1, 16),
                    UserRanking(2, "흰둥이", 0, 16)
                )
            ) { ranking ->
                RankingItem(ranking)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PuzzleGrid(puzzleData: PuzzleData, bitmap: Bitmap) {
    val totalCols = 4 // todo 퍼즐 열 수 고정
    val puzzleRows = puzzleData.puzzles.chunked(totalCols)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        puzzleRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // 조각들을 균등하게 배치
            ) {
                row.forEach { puzzle ->
                    val pieceBitmap = cropBitmapPiece(
                        bitmap,
                        puzzle.row,
                        puzzle.column,
                        puzzleData.totalPieces / totalCols,
                        totalCols
                    )
                    PuzzleItem(
                        puzzle = puzzle,
                        piece = pieceBitmap,
                        onPuzzleClick = { /* 클릭 이벤트 처리 */ },
                        modifier = Modifier.weight(1f))
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

@Composable
fun AttemptsSection(attemptCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = "횟수: $attemptCount",
            fontSize = 14.sp
        )
    }
}

@Composable
fun RankingItem(ranking: UserRanking) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(16.dp),
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
    val rank: Int,
    val nickname: String,
    val filledCount: Int,
    val totalPieces: Int
)

fun rememberPlaceholderBitmap(): Bitmap {
    return createBitmap(100, 100).apply {
        eraseColor(android.graphics.Color.GRAY) // 간단한 회색 비트맵
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
fun LoadingPlaceholder() { // todo 진짜 로딩으로 바꾸기
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("퍼즐 이미지 로딩 중...")
    }
}

// todo 다른 파일로 분리
@Composable
fun PuzzleItem(
    puzzle: Puzzle,
    piece: ImageBitmap?,
    onPuzzleClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (puzzle.filledBy != null && piece != null) {
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

// Bitmap 자르기
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

// ---------------------------------
// Preview용 더미 화면
// ---------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewPuzzleScreen() {
    val grayBitmap = remember {
        rememberPlaceholderBitmap()
    }

    val dummyPuzzles = List(16) { index ->
        Puzzle(
            pieceId = index + 1,
            row = (index / 4) + 1,
            column = (index % 4) + 1,
            filledBy = if (index % 2 == 0) User(1, "UserA") else null,
            filledAt = null
        )
    }
    val dummyPuzzleData =
        PuzzleData(puzzles = dummyPuzzles, totalPieces = 16, filledCount = 10)

    Scaffold(
        topBar = {
            // todo -> nullable
            TopBar(dummyPuzzleData.filledCount, dummyPuzzleData.totalPieces)
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // 퍼즐 부분만 별도의 컴포넌트로 분리
                PuzzleGrid(
                    puzzleData = dummyPuzzleData,
                    bitmap = grayBitmap
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // 횟수 표시
                AttemptsSection(attemptCount = 2) // 예시로 2를 사용

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 랭킹 리스트 부분
            items(
                items = listOf(
                    UserRanking(1, "맹구", 4, 16),
                    UserRanking(2, "짱구", 2, 16),
                    UserRanking(2, "철수", 2, 16),
                    UserRanking(3, "훈이", 1, 16),
                    UserRanking(3, "유리", 1, 16),
                    UserRanking(2, "흰둥이", 0, 16)
                )
            ) { ranking ->
                RankingItem(ranking)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}