// ui/home/component/FriendList.kt
package com.example.irumi.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.home.Friend

@Composable
fun FriendList(
    friends: List<Friend>,
    selected: Friend,
    brand: Color,
    onSelect: (Friend) -> Unit,
    onAddClick: () -> Unit,
    onLongPress: (Friend) -> Unit,          // 🔹 추가
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(friends, key = { it.id }) { friend ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(84.dp)
                    .combinedClickable(
                        onClick = { onSelect(friend) },
                        onLongClick = {
                            if (friend.id != 0) onLongPress(friend) // "나"는 제외
                        }
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF7F8FA))
                        .border(
                            width = if (friend == selected) 2.dp else 1.dp,
                            color = if (friend == selected) brand else Color(0xFFE6E8EC),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (friend == selected) brand.copy(alpha = .15f) else Color(0xFFEDEFF3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐹", fontSize = 22.sp)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(friend.name, fontSize = 12.sp)
            }
        }

        // + 버튼
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF1F3F5))
                        .combinedClickable(
                            onClick = onAddClick,
                            onLongClick = {} // 무시
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontSize = 26.sp, color = Color(0xFF98A2B3))
                }
                Spacer(Modifier.height(6.dp))
                Text("추가", fontSize = 12.sp, color = Color(0xFF98A2B3))
            }
        }
    }
}
