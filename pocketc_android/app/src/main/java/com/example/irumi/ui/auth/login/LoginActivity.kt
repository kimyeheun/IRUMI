package com.example.irumi.ui.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.irumi.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.irumi.core.pref.TokenStore

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    @Inject lateinit var tokenStore: TokenStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 자동 로그인: 토큰 있고 자동로그인 on이면 바로 홈 이동
        if (tokenStore.autoLogin && !tokenStore.accessToken.isNullOrBlank()) {
            goHome()
            return
        }

        setContent {
            LoginRoute(
                onSuccess = { goHome() },
                onBack = { finish() }
            )
        }
    }

    private fun goHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
