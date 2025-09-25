// ui/MainActivity.kt
package com.example.irumi.ui

import android.app.Activity
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.irumi.core.navigation.Events
import com.example.irumi.core.navigation.Home
import com.example.irumi.core.navigation.MyPage
import com.example.irumi.core.navigation.Stats
import com.example.irumi.ui.component.navBar.BottomNavBar
import com.example.irumi.ui.events.EventsScreen
import com.example.irumi.ui.home.HomeScreen
import com.example.irumi.ui.intro.IntroActivity
import com.example.irumi.ui.main.MainNavigator
import com.example.irumi.ui.main.rememberMainNavigator
import com.example.irumi.ui.payments.navigation.paymentDetailNavGraph
import com.example.irumi.ui.payments.navigation.paymentsNavGraph
import com.example.irumi.ui.profile.MyProfile
import com.example.irumi.ui.stats.StatsRoute
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

    Scaffold(
        bottomBar = {
            BottomNavBar(
                visible = navigator.shouldShowBottomBar(),
                navController = navigator.navController
            )
        }
    ) { inner ->
        NavHost(
            navController = navigator.navController,
            startDestination = Home, // 앱 시작 시 보여줄 화면
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentAlignment = Alignment.Center,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable<Home> {
                HomeScreen(brand)
            }
            composable<Stats> {
                val ctx = LocalContext.current
                StatsRoute(
                    brand = brand,
                    onLoggedOut = {
                        val intent = Intent(ctx, IntroActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        ctx.startActivity(intent)

                        (ctx as? Activity)?.finish()
                    }
                )
            }
            composable<Events> {
                EventsScreen(brand)
            }
            composable<MyPage> {
                val ctx = LocalContext.current
                MyProfile(
                    onLoggedOut = {
                        val intent = Intent(ctx, IntroActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        ctx.startActivity(intent)

                        (ctx as? Activity)?.finish()
                    }
                )
            }

            paymentsNavGraph(
                onNavigateToDetail = {
                    navigator.navigateToPaymentDetail(it)
                },
                paddingValues = inner
            )

            paymentDetailNavGraph(
                paddingValues = inner,
                navigateUp = navigator::navigateUp
            )
        }
    }
}
