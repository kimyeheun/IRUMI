package com.example.irumi.ui.events

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irumi.domain.entity.EventEntity
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NoEventScreen(viewModel: EventViewModel = hiltViewModel()) {
    // TODO 뷰모델 연결
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf<DialogType?>(null) }

    // todo 뷰모델 연결
    val event = EventEntity(
        eventId = 1,
        eventName = "맹구 퍼즐 미션",
        eventDescription = "참여자들이 퍼즐을 맞추는 협동 이벤트입니다. 친구들과 함께 보상을 획득하세요!",
        eventImageUrl = "https://mblogthumb-phinf.pstatic.net/MjAyMzEwMDhfMjMz/MDAxNjk2NzMyNTA3NzM1.O5iVGUwOEGFbxoqzH9H5H2qwFmbLNdOR7PmuuNE2PMAg.eY7eLpHanrC_AWz-9T2VCZamarnMq_5i6MBHboR2Z1Ug.JPEG.qmfosej/IMG_7989.JPG?type=w800",
        badgeName = "퍼즐왕",
        badgeDescription = "퍼즐을 모두 완성하면 지급되는 배지입니다.",
        startAt = "2025-08-31T00:00:00Z",
        endAt = "2025-09-10T23:59:59Z"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_menu_gallery), // TODO 실제 이미지로 교체 필요
            contentDescription = "Event Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = event.eventName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "기간: ${formatDate(event.startAt)} ~ ${formatDate(event.endAt)}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = event.eventDescription,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏆 보상: ${event.badgeName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.badgeDescription,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                text = "방 입장",
                onClick = {
                    dialogType = DialogType.ENTER_ROOM
                    showDialog = true
                }
            )
            ActionButton(
                text = "방 생성",
                onClick = {
                    dialogType = DialogType.CREATE_ROOM
                    showDialog = true
                }
            )
        }
    }

    if (showDialog) {
        when (dialogType) {
            DialogType.ENTER_ROOM -> {
                RoomCodeInputDialog(
                    onDismissRequest = { showDialog = false },
                    onConfirm = { roomCode ->
                        viewModel.enterRoom(roomCode)
                        showDialog = false
                    }
                )
            }
            DialogType.CREATE_ROOM -> {
                RoomCreationDialog(
                    onDismissRequest = { showDialog = false },
                    onConfirm = { maxMembers ->
                        viewModel.createRoom(maxMembers)
                        showDialog = false
                    }
                )
            }
            null -> {}
        }
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun RoomCodeInputDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var roomCode by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "방 코드 입력",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "유효한 방 코드를 입력해주세요.")

                OutlinedTextField(
                    value = roomCode,
                    onValueChange = {
                        roomCode = it
                        isValid = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("방 코드") },
                    isError = !isValid,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp)
                )

                if (!isValid) {
                    Text(
                        text = "유효하지 않은 방코드 입니다.",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onConfirm(roomCode)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("입장")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun RoomCreationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int) -> Unit,
    maxMembers: Int = 5,
    minMembers: Int = 2
) {
    var currentMembers by remember { mutableStateOf(minMembers) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "방 생성",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "최대 인원을 설정해주세요.",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { if (currentMembers > minMembers) currentMembers-- },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        text = "$currentMembers",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { if (currentMembers < maxMembers) currentMembers++ },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentMembers) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("생성")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    )
}

// TODO Util
private fun formatDate(dateString: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val formatter = SimpleDateFormat("MM.dd", Locale.getDefault())
    val date = parser.parse(dateString) ?: return ""
    return formatter.format(date)
}

enum class DialogType {
    ENTER_ROOM, CREATE_ROOM
}
