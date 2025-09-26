package com.example.irumi.ui.auth.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.irumi.ui.MainActivity
import com.example.irumi.ui.auth.login.LoginActivity
import com.example.irumi.core.pref.TokenStore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {

    @Inject lateinit var tokenStore: TokenStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 이미 로그인 + 자동로그인 ON 이면 바로 홈
        if (tokenStore.autoLogin && !tokenStore.accessToken.isNullOrBlank()) {
            goToHome()
            return
        }

        setContent {
            RegisterRoute(
                onDone = { goToLogin() },   // “회원가입 완료 → 로그인으로” 플로우가 필요할 때
                onGoHome = { goToHome() },  // “회원가입 성공 후 바로 홈 진입” 플로우
                onBack = { finish() }
            )
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun goToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
