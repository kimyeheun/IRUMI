package com.example.irumi.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irumi.core.pref.TokenStore
import com.example.irumi.data.dto.request.auth.AuthEditRequest
import com.example.irumi.data.dto.request.auth.LoginRequest
import com.example.irumi.data.dto.request.auth.SignUpRequest
import com.example.irumi.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    var loading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set
    var isLoggedIn by mutableStateOf(!tokenStore.accessToken.isNullOrBlank()); private set

    /** 앱 시작 시 자동 로그인 조건 확인 */
    fun tryAutoLogin(): Boolean =
        tokenStore.autoLogin && !tokenStore.accessToken.isNullOrBlank()

    fun signUp(name: String, email: String, pw: String, budget: Int, remember: Boolean) =
        launch {
            repo.signUp(SignUpRequest(name, email, pw, budget))
                .onSuccess {
                    Timber.d("!!! 회원가입 성공")
                    tokenStore.save(it.accessToken, it.refreshToken)
                    tokenStore.autoLogin = remember
                    tokenStore.email = email
                    isLoggedIn = true
                }
                .onFailure {
                    Timber.d("!!! 회원가입 실패 ${it.message}")
                    error = it.message
                }
        }

    fun login(email: String, pw: String, remember: Boolean) =
        launch {
            repo.login(LoginRequest(email, pw))
                .onSuccess { env ->
                    // 로그인 응답이 access 단일 토큰인 스펙
                    tokenStore.save(env.accessToken, tokenStore.refreshToken)
                    tokenStore.autoLogin = remember
                    tokenStore.email = email
                    isLoggedIn = true
                }
                .onFailure {
                    Timber.d("로그인 실패 ${it.message}")
                    error = it.message
                }
        }

    fun logout() = launch {
        repo.logout()
            .onSuccess {
                tokenStore.clear()
                isLoggedIn = false
            }
            .onFailure { error = it.message }
    }

    fun reissue() = launch {
        repo.reissue()
            .onSuccess { tokenStore.save(it.accessToken, it.refreshToken) }
            .onFailure { error = it.message }
    }

    fun updateMe(req: AuthEditRequest) = launch {
        repo.updateMe(req).onFailure { error = it.message }
    }

    private fun launch(block: suspend () -> Unit) = viewModelScope.launch {
        loading = true; error = null
        try { block() } finally { loading = false }
    }
}
