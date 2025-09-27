package com.example.irumi.ui.events

import android.R
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.ui.theme.BrandGreen
import com.example.irumi.ui.theme.BrandGreen40
import java.text.SimpleDateFormat
import java.util.Locale

// 토스 컬러 시스템
private val TossBlueLight = Color(0xFFF2F6FF)
private val TossGray50 = Color(0xFFF9FAFB)
private val TossGray100 = Color(0xFFF1F3F5)
private val TossGray300 = Color(0xFFD1D6DB)
private val TossGray500 = Color(0xFF8B95A1)
private val TossGray700 = Color(0xFF4E5968)
private val TossGray900 = Color(0xFF191F28)

@Composable
fun NoEventScreen(viewModel: EventViewModel = hiltViewModel(), eventEntity: EventEntity) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf<DialogType?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TossGray50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 이벤트 이미지 섹션
            EventImageSection(eventEntity = eventEntity)

            Spacer(modifier = Modifier.height(32.dp))

            // 이벤트 정보 섹션
            EventInfoSection(eventEntity = eventEntity)

            Spacer(modifier = Modifier.height(24.dp))

            // 보상 정보 카드
            RewardInfoCard(eventEntity = eventEntity)

            Spacer(modifier = Modifier.height(40.dp))

            // 액션 버튼들
            ActionButtonsSection(
                onEnterRoom = {
                    dialogType = DialogType.ENTER_ROOM
                    showDialog = true
                },
                onCreateRoom = {
                    dialogType = DialogType.CREATE_ROOM
                    showDialog = true
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showDialog) {
        when (dialogType) {
            DialogType.ENTER_ROOM -> {
                TossStyleRoomCodeDialog(
                    onDismissRequest = { showDialog = false },
                    onConfirm = { roomCode ->
                        viewModel.enterRoom(roomCode)
                        showDialog = false
                    }
                )
            }
            DialogType.CREATE_ROOM -> {
                TossStyleRoomCreationDialog(
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
private fun EventImageSection(eventEntity: EventEntity) {
    Surface(
        modifier = Modifier.size(120.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(eventEntity.badgeImageUrl)
                .crossfade(true)
                .placeholder(R.drawable.ic_menu_gallery)
                .build(),
            contentDescription = "Event Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}

@Composable
private fun EventInfoSection(eventEntity: EventEntity) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = eventEntity.eventName,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TossGray900,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TossGray100,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "${formatDate(eventEntity.startAt)} ~ ${formatDate(eventEntity.endAt)}",
                fontSize = 14.sp,
                color = TossGray700,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = eventEntity.eventDescription,
            fontSize = 16.sp,
            color = TossGray700,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
private fun RewardInfoCard(eventEntity: EventEntity) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TossBlueLight,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "🏆 ${eventEntity.badgeName}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Text(
                text = eventEntity.badgeDescription,
                fontSize = 14.sp,
                color = TossGray500,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onEnterRoom: () -> Unit,
    onCreateRoom: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TossActionButton(
            text = "방 입장하기",
            icon = Icons.Rounded.Login,
            isPrimary = true,
            onClick = onEnterRoom
        )

        TossActionButton(
            text = "방 만들기",
            icon = Icons.Rounded.Add,
            isPrimary = false,
            onClick = onCreateRoom
        )
    }
}

@Composable
private fun TossActionButton(
    text: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) BrandGreen else Color.White,
            contentColor = if (isPrimary) Color.White else TossGray700
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isPrimary) 0.dp else 1.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TossStyleRoomCodeDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var roomCode by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "방 코드를 입력해주세요",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TossGray900,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "친구가 공유한 방 코드를 입력하면\n바로 참여할 수 있어요",
                    fontSize = 14.sp,
                    color = TossGray500,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = roomCode,
                    onValueChange = {
                        roomCode = it
                        isValid = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "방 코드",
                            color = TossGray300
                        )
                    },
                    isError = !isValid,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen,
                        unfocusedBorderColor = BrandGreen40,
                        errorBorderColor = Color.Red
                    )
                )

                if (!isValid) {
                    Text(
                        text = "올바른 방 코드를 입력해주세요",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomCode.isNotBlank()) {
                        onConfirm(roomCode)
                    } else {
                        isValid = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                enabled = roomCode.isNotBlank()
            ) {
                Text(
                    "입장하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun TossStyleRoomCreationDialog(
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
                text = "방을 만들어주세요",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TossGray900,
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
                    text = "함께할 친구들의 수를 정해주세요\n최대 ${maxMembers}명까지 가능해요",
                    fontSize = 14.sp,
                    color = TossGray500,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TossGray50,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Surface(
                            onClick = { if (currentMembers > minMembers) currentMembers-- },
                            shape = CircleShape,
                            color = if (currentMembers > minMembers) BrandGreen else TossGray300,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = "${currentMembers}명",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TossGray900
                        )

                        Surface(
                            onClick = { if (currentMembers < maxMembers) currentMembers++ },
                            shape = CircleShape,
                            color = if (currentMembers < maxMembers) BrandGreen else TossGray300,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentMembers) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text(
                    "방 만들기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

// 유틸리티 함수
private fun formatDate(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("MM.dd", Locale.getDefault())
        val date = parser.parse(dateString) ?: return dateString
        formatter.format(date)
    } catch (e: Exception) {
        dateString
    }
}

enum class DialogType {
    ENTER_ROOM, CREATE_ROOM
}