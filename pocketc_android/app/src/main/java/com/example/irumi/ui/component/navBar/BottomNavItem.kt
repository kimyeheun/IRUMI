package com.example.irumi.ui.component.navBar

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.irumi.core.navigation.Route
import com.example.irumi.ui.theme.BrandGreen

sealed class BottomNavItem(val label: String, val icon: ImageVector, val route: Route) {
    data object Home : BottomNavItem("홈", Icons.Filled.Home, com.example.irumi.core.navigation.Home)
    data object Payments : BottomNavItem("결제 내역", Icons.Filled.List, com.example.irumi.core.navigation.Payments)
    data object Stats : BottomNavItem("통계", Icons.Filled.BarChart, com.example.irumi.core.navigation.Stats)
    data object Events : BottomNavItem("이벤트", Icons.Filled.Event, com.example.irumi.core.navigation.Events)
    data object MyPage : BottomNavItem("마이페이지", Icons.Filled.Person, com.example.irumi.core.navigation.MyPage)
}

@Composable
fun BottomNavBar(
//    items: List<BottomNavItem>,
//    selected: BottomNavItem,
//    onSelect: (BottomNavItem) -> Unit
    navController: NavController, // `selected`와 `onSelect` 대신 `navController`를 받음
    items: List<BottomNavItem> = listOf( // `items`를 기본값으로 설정
        BottomNavItem.Home,
        BottomNavItem.Payments,
        BottomNavItem.Stats,
        BottomNavItem.Events,
        BottomNavItem.MyPage
    ),
    visible: Boolean,
//    currentTab: MainTab,
//    tabs: ImmutableList<MainTab>,
//    onTabSelected: (MainTab) -> Unit
) {
    val gray = Color(0xFF9E9E9E) // 미선택 텍스트/아이콘

    // 1. 현재 네비게이션 경로를 상태로 관찰합니다.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    AnimatedVisibility(
        visible = visible
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
//            val isSelected = selected::class == item::class
                // 2. 현재 경로가 아이템의 경로와 일치하면 선택된 상태로 간주합니다.
                val isSelected = currentDestination?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true


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
//                onClick = { onSelect(item) },
                    // 3. 클릭 시 `navController.navigate()`를 호출해 화면을 이동
                    onClick = {
                        navController.navigate(item.route) {
                            // 백 스택 중복 생성을 방지하고, 메인 탭 전환을 최적화하는 옵션
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
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

}
