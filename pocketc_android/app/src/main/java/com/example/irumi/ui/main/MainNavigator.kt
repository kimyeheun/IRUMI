package com.example.irumi.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.irumi.core.navigation.Events
import com.example.irumi.core.navigation.Home
import com.example.irumi.core.navigation.Payments
import com.example.irumi.core.navigation.Route
import com.example.irumi.core.navigation.Stats
import com.example.irumi.ui.payments.navigation.navigateToPaymentDetail
import kotlin.reflect.KClass

val bottomNavScreens: List<KClass<out Route>> = listOf(
    Home::class,
    Payments::class,
    Stats::class,
    Events::class
)

class MainNavigator(
    val navController: NavHostController
) {
    @Composable
    fun shouldShowBottomBar(): Boolean {
        val currentDestination = navController
            .currentBackStackEntryAsState()
            .value
            ?.destination

        return currentDestination?.hierarchy?.any { dest ->
            bottomNavScreens.any { it.qualifiedName == dest.route }
        } == true
    }

    fun navigateToPaymentDetail(paymentId: Int) {
        navController.navigateToPaymentDetail(paymentId)
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}

@Composable
fun rememberMainNavigator(
    navController: NavHostController = rememberNavController()
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}