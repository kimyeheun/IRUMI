package com.example.irumi.ui.stats

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irumi.ui.auth.AuthViewModel
import com.example.irumi.ui.component.button.PrimaryButton

/** 컨테이너: ViewModel과 연결 + 로그아웃 성공 시 콜백 */
@Composable
fun StatsRoute(
    brand: Color,
    onLoggedOut: () -> Unit,            // 로그인 화면으로 이동 등
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loading = viewModel.loading
    val error = viewModel.error
    val isLoggedIn = viewModel.isLoggedIn

    // 로그아웃 성공 감지 → 외부로 알림
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) onLoggedOut()
    }

    // 에러 토스트
    val ctx = LocalContext.current
    LaunchedEffect(error) {
        error?.let { Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show() }
    }

    StatsScreen(
        brand = brand,
        loading = loading,
        onLogout = { viewModel.logout() }
    )
}

/** 프리젠테이션: UI만 담당 */
@Composable
fun StatsScreen(
    brand: Color,
    loading: Boolean,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("통계", fontSize = 28.sp, color = brand)

        Spacer(Modifier.height(8.dp))

        PrimaryButton(
            text = if (loading) "로그아웃 중..." else "로그아웃",
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            loading = loading
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsScreenPreview() {
    StatsScreen(
        brand = Color(0xFF00C853),
        loading = false,
        onLogout = {}
    )
}
