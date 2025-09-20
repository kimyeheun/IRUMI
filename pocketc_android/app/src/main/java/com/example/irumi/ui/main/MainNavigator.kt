package com.example.irumi.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.irumi.ui.payments.navigation.navigateToPaymentDetail

val bottomNavScreens = listOf(
    "home",
    "payments",
    "stats",
    "events"
)

class MainNavigator(
    val navController: NavHostController
) {
    @Composable
    fun shouldShowBottomBar(): Boolean {
        val currentRoute = navController
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route
            ?.substringBefore("?") // 쿼리 파라미터 제거

        return currentRoute != null && currentRoute in bottomNavScreens
    }

    fun navigateToPaymentDetail() {
        navController.navigateToPaymentDetail()
    }
}

@Composable
fun rememberMainNavigator(
    navController: NavHostController = rememberNavController()
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}