package com.example.irumi.ui.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.events.SampleColors
import com.example.irumi.ui.payments.TossColors
import com.example.irumi.ui.theme.BrandGreen
import java.text.DecimalFormat

fun formatToKoreanCurrency(amount: Int): String {
    if (amount == 0) return ""
    val man = amount / 10000
    return if (man > 0) "${man}만원" else "${DecimalFormat("#,###").format(amount)}원"
}

@Composable
fun EnhancedBudgetInputScreen(onSubmit: (Int) -> Unit) {
    // 쉼표 없는 숫자 문자열을 상태로 관리
    var budgetInput by remember { mutableStateOf("0") }
    val budgetValue: Int = budgetInput.toIntOrNull() ?: 0
    val formatter = remember { DecimalFormat("#,###") }

    val keypadItems = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "00", "0", "←")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 상단 컨텐츠 영역 ---
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "월 예산을 입력해주세요",
                style = MaterialTheme.typography.titleLarge,
                color = TossColors.OnSurface
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 입력되는 예산 (크고 굵게)
            Text(
                text = "${formatter.format(budgetValue)} 원",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = BrandGreen
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 한글 금액 표시
            Text(
                text = formatToKoreanCurrency(budgetValue),
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.height(24.dp)
            )
        }

        // --- 하단 키패드 영역 ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            items(keypadItems) { item ->
                KeypadButton(
                    text = item,
                    onClick = {
                        when (item) {
                            "←" -> { // 지우기
                                if (budgetInput.length > 1) {
                                    budgetInput = budgetInput.dropLast(1)
                                } else {
                                    budgetInput = "0"
                                }
                            }
                            else -> { // 숫자 입력
                                val newText = if (budgetInput == "0") item else budgetInput + item
                                // 최대 1,000만원 (8자리) 제한
                                if (newText.length <= 8) {
                                    budgetInput = newText
                                }
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 확인 버튼
        Button(
            onClick = { onSubmit(budgetValue) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = budgetValue > 0,
            colors = ButtonColors(
                containerColor = BrandGreen,
                contentColor = Color.White,
                disabledContainerColor = SampleColors.Gray100,
                disabledContentColor = TossColors.OnSurface
            )
        ) {
            Text("확인", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun KeypadButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.Gray.copy(alpha = 0.1f))
            .clickable(onClick = onClick)
            .aspectRatio(1.5f), // 버튼 비율
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EnhancedBudgetInputScreenPreview() {
    EnhancedBudgetInputScreen(onSubmit = {})
}