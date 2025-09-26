package com.example.irumi.ui.component.navBar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.irumi.ui.theme.BrandGreen

sealed class BottomNavItem(val label: String, val icon: ImageVector) {
    data object Home : BottomNavItem("홈", Icons.Filled.Home)
    data object Payments : BottomNavItem("결제 내역", Icons.Filled.List)
    data object Stats : BottomNavItem("통계", Icons.Filled.BarChart)
    data object Events : BottomNavItem("이벤트", Icons.Filled.Event)
}

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    selected: BottomNavItem,
    onSelect: (BottomNavItem) -> Unit
) {
    val gray = Color(0xFF9E9E9E) // 미선택 텍스트/아이콘

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = selected::class == item::class

            val circleColor by animateColorAsState(
                targetValue = if (isSelected) BrandGreen else Color.White,
                label = "nav-circle"
            )
            val iconTint by animateColorAsState(
                targetValue = if (isSelected) Color.White else gray,
                label = "nav-icon"
            )
            val labelColor by animateColorAsState(
                targetValue = if (isSelected) BrandGreen else gray,
                label = "nav-label"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(item) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(circleColor, CircleShape)
                                // ripple 제거 (원형만 강조)
                                .indication(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                },
                label = { Text(text = item.label, color = labelColor) },
                // 선택 인디케이터 바(밑줄) 제거
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = Color.White,    // 실제 틴트는 iconTint로 애니메이션
                    selectedTextColor = BrandGreen,
                    unselectedIconColor = gray,
                    unselectedTextColor = gray
                ),
                interactionSource = remember { MutableInteractionSource() }
            )
        }
    }
}
