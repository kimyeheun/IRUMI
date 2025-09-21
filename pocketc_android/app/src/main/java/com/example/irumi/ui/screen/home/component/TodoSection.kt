package com.example.irumi.ui.screen.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.theme.BrandGreen

@Composable
fun TodoSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { /* 탭 전환 */ },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)) {
                Text("데일리")
            }
            Button(onClick = { /* 탭 전환 */ },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)) {
                Text("주간/월간")
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items((1..8).toList()) { i ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(checked = i == 3, onCheckedChange = {})
                    Text("미션 $i", fontSize = 16.sp)
                }
            }
        }
    }
}
