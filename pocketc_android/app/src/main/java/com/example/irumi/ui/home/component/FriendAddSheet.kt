package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.irumi.ui.payments.TossColors
import com.example.irumi.ui.theme.BrandGreen

@Preview(showBackground = true)
@Composable
fun FriendAddSheetPreview() {
    FriendAddSheet(
        onDismiss = {},
        onFollow = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendAddSheet(
    onDismiss: () -> Unit,
    onFollow: (String) -> Unit,
    isProcessing: Boolean = false,  // 팔로우 요청 중 로딩 표시
    error: String? = null           // 오류 메시지 표시
) {
    var query by remember { mutableStateOf("") } // targetUserId 입력값

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            Text("친구 추가", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = {
                    if (it.length <= 8) {
                        query = it
                    }
                },
                label = { Text("유저 코드 입력") },
                placeholder = { Text("예: A134B0F2") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                trailingIcon = {
                    if (query.isNotEmpty() && !isProcessing) {
                        TextButton(onClick = { query = "" }) { Text("지우기") }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TossColors.Primary,   // 포커스 됐을 때 테두리
                    unfocusedBorderColor = Color.Gray,         // 평소 테두리
                    cursorColor = TossColors.Primary,          // 커서 색
                    focusedLabelColor = TossColors.Primary,    // 포커스 시 라벨 색
                    unfocusedLabelColor = Color.Gray           // 평소 라벨 색
                )
            )
            Spacer(Modifier.height(8.dp))

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(6.dp))
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White, // 버튼 배경색
                        contentColor = BrandGreen   // 텍스트 색
                    )
                ) { Text("취소") }

                Button(
                    onClick = {
                        if (query.isNotEmpty()) {
                            onFollow(query)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen, // 버튼 배경색
                        contentColor = Color.White   // 텍스트 색
                    )
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("팔로우")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
