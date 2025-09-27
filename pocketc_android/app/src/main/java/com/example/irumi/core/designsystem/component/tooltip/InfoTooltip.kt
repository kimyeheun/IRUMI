package com.example.irumi.core.designsystem.component.tooltip

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.RichTooltipColors
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTooltip(
    title: String,
    description: String,
    iconSize: Int = 18,
    iconTint: Color = Color(0xFFAAC0BC)
) {
    val tooltipState = rememberTooltipState()
    val coroutineScope = rememberCoroutineScope()

    TooltipBox(
        state = tooltipState,
        tooltip = {
            RichTooltip(
                title = { Text(title) },
                colors = RichTooltipColors(
                    containerColor = Color(0xFF2C3137),
                    contentColor = Color.White,
                    titleContentColor = Color.White,
                    actionContentColor = Color.White
                )
            ) {
                Text(description)
            }
        },
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider()
    ) {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    if (tooltipState.isVisible) tooltipState.dismiss()
                    else {
                        tooltipState.show()
                        delay(5000L)
                    }
                }
            },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "$title 정보 보기",
                tint = iconTint,
                modifier = Modifier.size(iconSize.dp)
            )
        }
    }
}