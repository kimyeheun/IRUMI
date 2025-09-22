package com.example.irumi.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.irumi.ui.home.component.FriendCompareSection
import com.example.irumi.ui.home.component.FriendList
import com.example.irumi.ui.home.component.MyScoreSection
import com.example.irumi.ui.home.component.StreakSection
import com.example.irumi.ui.home.component.TodoSection
import com.example.irumi.ui.theme.BrandGreen

// boolean 으로 true/false 데일리 미션 (ai 가 추천 해주는 5가지 미션)

data class Friend(val id: Int, val name: String)

@Composable
fun HomeScreen(brand: Color = BrandGreen) {
    // 샘플 데이터(0 == 나)
    val friends = remember {
        listOf(Friend(0, "나"), Friend(1, "민수"), Friend(2, "나연"))
    }
    var selectedFriend by remember { mutableStateOf(friends.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        FriendList(
            friends = friends,
            selected = selectedFriend,
            brand = brand,
            onSelect = { selectedFriend = it },
            onAddClick = { /* TODO: 친구 추가 */ }
        )

        Spacer(Modifier.height(16.dp))

        if (selectedFriend.id == 0) {
            MyScoreSection(score = 81)
            Spacer(Modifier.height(12.dp))
            TodoSection()
            Spacer(Modifier.height(16.dp))
            StreakSection()
        } else {
            FriendCompareSection(
                myScore = 81,
                friendScore = 92,
                friendName = selectedFriend.name
            )
            Spacer(Modifier.height(16.dp))
            StreakSection(friendName = selectedFriend.name)
        }
    }
}
