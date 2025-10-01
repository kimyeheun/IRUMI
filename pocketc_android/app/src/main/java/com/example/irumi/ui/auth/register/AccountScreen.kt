package com.example.irumi.ui.auth.register

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.theme.BrandGreen

data class Bank(
    val code: String,
    val name: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    name: String,
    onConfirmClick: () -> Unit
) {
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var accountNumber by remember { mutableStateOf("") }
    var accountAlias by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val accountHolder = name

    val banks = listOf(
        Bank("KB", "KB국민은행", Color(0xFFFFB800)),
        Bank("신한", "신한은행", Color(0xFF0066CC)),
        Bank("우리", "우리은행", Color(0xFF4B9FE7)),
        Bank("하나", "KEB하나은행", Color(0xFF00A651)),
        Bank("기업", "IBK기업은행", Color(0xFF003876)),
        Bank("농협", "NH농협은행", Color(0xFF00A84C)),
        Bank("SC", "SC제일은행", Color(0xFF0066B3)),
        Bank("시티", "시티은행", Color(0xFF004B87))
    )

    val isFormValid = selectedBank != null && accountNumber.length >= 10

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = "계좌 연동",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Bank Selection
            BankSelectionField(
                selectedBank = selectedBank,
                isExpanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it },
                onBankSelected = {
                    selectedBank = it
                    isDropdownExpanded = false
                },
                banks = banks
            )

            // Account Number Input
            AccountNumberField(
                value = accountNumber,
                onValueChange = {
                    accountNumber = it.filter { char -> char.isDigit() || char == '-' }
                }
            )

            // Account Holder Display (shown when account number is valid)
            if (selectedBank != null && accountNumber.length >= 10) {
                AccountHolderField(accountHolder = accountHolder)
            }

            // Account Alias Input
            AccountAliasField(
                value = accountAlias,
                onValueChange = {
                    if (it.length <= 15) accountAlias = it
                }
            )

            // Information Card
            InfoCard()
        }

        // Bottom Button
        BottomRegisterButton(
            isEnabled = isFormValid,
            onClick = onConfirmClick
        )
    }
}

@Composable
fun BankSelectionField(
    selectedBank: Bank?,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onBankSelected: (Bank) -> Unit,
    banks: List<Bank>
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300)
    )

    Column {
        Text(
            text = "은행 선택",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color(0xFF1D1D1F)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!isExpanded) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedBank != null) BrandGreen.copy(alpha = 0.1f) else Color(0xFFF8FAF9)
                ),
                border = BorderStroke(
                    2.dp,
                    if (selectedBank != null || isExpanded) BrandGreen else Color(0xFFE5EAE9)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedBank?.name ?: "은행을 선택해주세요",
                        color = if (selectedBank != null) Color.Black else Color(0xFF8E8E93),
                        fontSize = 16.sp
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "드롭다운",
                        modifier = Modifier.rotate(rotationAngle),
                        tint = Color(0xFF8E8E93)
                    )
                }
            }

            if (isExpanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 72.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 240.dp)
                    ) {
                        items(banks) { bank ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onBankSelected(bank) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            color = bank.color,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = bank.code,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = bank.name,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountNumberField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = "계좌번호",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color(0xFF1D1D1F)
        )

        Spacer(modifier = Modifier.height(8.dp))

        AccountNumberTextField(
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun AccountNumberTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    // 입력값이 비어있지 않고, 10자리 미만일 때를 에러 상태로 정의
    val isError = value.isNotEmpty() && value.length < 10

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("계좌번호를 입력해주세요") },
        // isError 상태에 따라 에러를 표시
        isError = isError,
        // isError일 때 보여줄 보조 텍스트
        supportingText = {
            if (isError) {
                Text(
                    text = "계좌번호는 10~14자리여야 합니다.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else BrandGreen,
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFFE5EAE9),
            focusedContainerColor = BrandGreen.copy(alpha = 0.1f),
            unfocusedContainerColor = Color(0xFFF8FAF9)
        ),
        singleLine = true
    )
}

@Composable
fun AccountHolderField(accountHolder: String) {
    Column {
        Text(
            text = "예금주명",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color(0xFF1D1D1F)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF2F2F7)
            )
        ) {
            Text(
                text = accountHolder,
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun AccountAliasField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Row {
            Text(
                text = "계좌 별칭",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color(0xFF1D1D1F)
            )
            Text(
                text = " (선택)",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("생활비 계좌, 용돈 계좌 등") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandGreen,
                unfocusedBorderColor = Color(0xFFE5EAE9),
                focusedContainerColor = BrandGreen.copy(alpha = 0.1f),
                unfocusedContainerColor = Color(0xFFF8FAF9)
            ),
            singleLine = true
        )

        if (value.isNotEmpty()) {
            Text(
                text = "${value.length}/15",
                fontSize = 12.sp,
                color = Color(0xFF8E8E93),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrandGreen.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = BrandGreen,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = "안전한 계좌 연동",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1D1F)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "금융위원회 인증을 받은 안전한 방식으로 계좌를 연동합니다.\n계좌 비밀번호나 개인정보는 저장되지 않습니다.",
                    fontSize = 12.sp,
                    color = BrandGreen,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun BottomRegisterButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Button(
            onClick = onClick,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isEnabled) BrandGreen else Color(0xFFE5E5EA),
                contentColor = if (isEnabled) Color.White else Color(0xFF8E8E93)
            )
        ) {
            Text(
                text = "계좌 연동하기",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}