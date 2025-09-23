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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.R
import com.example.irumi.core.pref.TokenStore
import com.example.irumi.ui.MainActivity
import com.example.irumi.ui.intro.IntroActivity
import com.example.irumi.ui.theme.BrandGreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.math.min

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    @Inject lateinit var tokenStore: TokenStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 자동 로그인 조건 계산 (토큰 존재 + autoLogin ON)
        val goHome = tokenStore.autoLogin && !tokenStore.accessToken.isNullOrBlank()

        setContent {
            // 1.5초 후 분기 이동
            LaunchedEffect(Unit) {
                delay(1500)
                val next = if (goHome) MainActivity::class.java else IntroActivity::class.java
                startActivity(Intent(this@SplashActivity, next))
                finish()
            }

            // 화면 크기 기준으로 캐릭터 크기 계산
            val cfg = LocalConfiguration.current
            val base = min(cfg.screenWidthDp, cfg.screenHeightDp)
            val characterSize = (base * 1f).dp   // 화면의 65% 정도로 크게

            // 랜덤 캐릭터 선택 (drawable에 있는 리소스 중에서)
            val characterRes = remember {
                listOf(
                    R.drawable.character_1,
                    R.drawable.character_5,
                    R.drawable.character_10,
                    R.drawable.character_50,
                    R.drawable.character_100,
                    R.drawable.character_500
                ).random()
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

                // 하단 우측에 랜덤 캐릭터 크게 배치 (일부 잘려 보이게 오프셋)
                Image(
                    painter = painterResource(id = characterRes),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(characterSize)     // 크게
                        .offset(x = 96.dp, y = 80.dp) // 오른쪽/아래로 더 밀어서 일부가 화면 밖으로 나가도록
                )

                // 하단 저작권 문구 (캐릭터 위에 보이게 마지막에 배치)
                Text(
                    text = "IRUMI. All rights reserved.",
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
