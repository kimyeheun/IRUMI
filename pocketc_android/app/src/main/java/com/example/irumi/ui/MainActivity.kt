package com.example.irumi.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.irumi.ui.component.navBar.BottomNavBar
import com.example.irumi.ui.component.navBar.BottomNavItem
import com.example.irumi.ui.screen.events.EventsScreen
import com.example.irumi.ui.screen.home.HomeScreen
import com.example.irumi.ui.screen.payments.PaymentsScreen
import com.example.irumi.ui.screen.stats.StatsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }
}

@Composable
fun MainScreen() {
    val brand = Color(0xFF4CAF93)

    val items = remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Payments,
            BottomNavItem.Stats,
            BottomNavItem.Events
        )
    }
    var selected by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = items,
                selected = selected,
                onSelect = { selected = it }
            )
        }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
            when (selected) {
                BottomNavItem.Home -> HomeScreen(brand)
                BottomNavItem.Payments -> PaymentsScreen(brand)
                BottomNavItem.Stats -> StatsScreen(brand)
                BottomNavItem.Events -> EventsScreen(brand)
            }
        }
    }
}
