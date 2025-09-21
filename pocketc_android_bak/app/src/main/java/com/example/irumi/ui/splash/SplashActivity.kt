package com.example.irumi.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.R
import com.example.irumi.ui.intro.IntroActivity
import com.example.irumi.ui.theme.BrandGreen
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1.5초 후 IntroActivity로 이동
            LaunchedEffect(Unit) {
                delay(1500)
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                finish()
            }

            // 전체 배경 박스
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BrandGreen)
            ) {
                // 중앙 로고와 버전 텍스트
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo_w),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ver. 1.0.0",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // 하단 저작권 문구
                Text(
                    text = "POCKETC. All rights reserved.",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 56.dp)
                )
            }
        }
    }
}
