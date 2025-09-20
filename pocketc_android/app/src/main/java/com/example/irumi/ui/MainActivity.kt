package com.example.irumi.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.irumi.ui.component.navBar.BottomNavBar
import com.example.irumi.ui.main.MainNavigator
import com.example.irumi.ui.main.rememberMainNavigator
import com.example.irumi.ui.payments.navigation.paymentDetailNavGraph
import com.example.irumi.ui.payments.navigation.paymentsNavGraph
import com.example.irumi.ui.screen.events.EventsScreen
import com.example.irumi.ui.screen.home.HomeScreen
import com.example.irumi.ui.screen.stats.StatsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }
}

@Composable
fun MainScreen(navigator: MainNavigator = rememberMainNavigator()) {
    val brand = Color(0xFF4CAF93)
//    val navController = rememberNavController()

    // 현재 경로를 관찰하여 BottomBar 표시 여부 결정
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route

//    val shouldShowBottomBar = remember(currentRoute) {
//        when (currentRoute) {
//            "payments", "home", "stats", "events" -> true
//            else -> false
//        }
//    }

//    val items = remember {
//        listOf(
//            BottomNavItem.Home,
//            BottomNavItem.Payments,
//            BottomNavItem.Stats,
//            BottomNavItem.Events
//        )
//    }
//    var selected by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                visible = navigator.shouldShowBottomBar(),
                navController = navigator.navController)
//            BottomNavBar(
//                items = items,
//                selected = selected,
//                onSelect = { selected = it }
//            )
        }
    ) { inner ->
        NavHost(
            navController = navigator.navController,
            startDestination = "home", // 앱 시작 시 보여줄 화면
            modifier = Modifier.fillMaxSize()
                .padding(inner),
            contentAlignment = Alignment.Center,
            enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None }
        ) {
            composable("home") {
                HomeScreen(brand)
            }
            composable("stats") {
                StatsScreen(brand)
            }
            composable("events") {
                EventsScreen(brand)
            }

            paymentsNavGraph(
                navigateToPaymentDetail = {
                    navigator.navigateToPaymentDetail()
                },
                paddingValues = inner
            )

            paymentDetailNavGraph(
                paddingValues = inner
            )
        }
//        Box(Modifier
//            .fillMaxSize()
//            .padding(inner), contentAlignment = Alignment.Center) {
//            when (selected) {
//                BottomNavItem.Home -> HomeScreen(brand)
//                BottomNavItem.Payments -> PaymentsScreen(brand)
//                BottomNavItem.Stats -> StatsScreen(brand)
//                BottomNavItem.Events -> EventsScreen(brand)
//            }
//        }
    }
}
