package com.example.irumi.ui.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendAddSheet(
    onDismiss: () -> Unit,
    onFollow: (Int) -> Unit,        // ViewModel.follow(targetUserId)
    isProcessing: Boolean = false,  // 팔로우 요청 중 로딩 표시
    error: String? = null           // 오류 메시지 표시
) {
    var query by remember { mutableStateOf("") } // targetUserId 입력값
    val isValid = remember(query) { query.toIntOrNull() != null }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            Text("친구 추가", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it.filter { ch -> ch.isDigit() } }, // 숫자만 허용
                label = { Text("User ID로 추가") },
                placeholder = { Text("예: 101") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                trailingIcon = {
                    if (query.isNotEmpty() && !isProcessing) {
                        TextButton(onClick = { query = "" }) { Text("지우기") }
                    }
                },
                modifier = Modifier.fillMaxWidth()
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
                    enabled = !isProcessing
                ) { Text("취소") }

                Button(
                    onClick = {
                        query.toIntOrNull()?.let { onFollow(it) }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessing && isValid
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
