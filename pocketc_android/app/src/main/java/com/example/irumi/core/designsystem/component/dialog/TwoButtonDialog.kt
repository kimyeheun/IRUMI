package com.example.irumi.core.designsystem.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.irumi.ui.payments.TossColors
import com.example.irumi.ui.theme.BrandGreen

// TODO 디자인 작업
@Composable
fun TwoButtonDialog(
    title: String,
    text: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirmFollow: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = title, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        },
        text = {
            Text(text = text, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        },
        confirmButton = {
            Button(
                onClick = onConfirmFollow,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen, // 버튼 배경색
                    contentColor = Color.White   // 텍스트 색
                )
            ) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = TossColors.Background, // 배경색
                    contentColor = BrandGreen              // 텍스트 색
                )
            ) {
                Text(text = dismissButtonText)
            }
        },
        containerColor = Color.White,
        modifier = Modifier.padding(16.dp)
    )
}