package com.example.irumi.ui.intro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irumi.ui.auth.login.LoginActivity
import com.example.irumi.ui.auth.register.RegisterActivity
import com.example.irumi.ui.component.button.PrimaryButton
import com.example.irumi.ui.theme.BrandGreen
import com.example.irumi.R

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntroScreen(
                onStartClick = {
                    startActivity(Intent(this, RegisterActivity::class.java))
                },
                onGoLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                },
                onTempClick = {
                    Toast.makeText(this, "시작하기 클릭!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun IntroScreen(
    onStartClick: () -> Unit,
    onGoLoginClick: () -> Unit,
    onTempClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.ui.graphics.Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(24.dp))

            // 로고 + 캐릭터 이미지 묶음
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.irumi_logo_c),
                    contentDescription = "Irumi Logo",
                    modifier = Modifier
                        .size(220.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // 로고와 붙여서 배치
                Image(
                    painter = painterResource(id = R.drawable.friends_all),
                    contentDescription = "Friends Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 시작 버튼
                PrimaryButton(
                    text = "시작하기",
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(Modifier.height(20.dp))

                // 로그인 안내
                Text(
                    text = "이룸이 회원이신가요?  로그인",
                    color = BrandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .clickable { onGoLoginClick() },
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(36.dp))

                // 약관 안내
                Text(
                    text = "이룸이에 로그인하면 이용약관에 동의하는 것으로 간주됩니다.\n" +
                            "회원 정보 처리 방식은 개인정보 처리방침 및 쿠키 정책에서 확인하세요.",
                    color = BrandGreen,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }
    }
}
