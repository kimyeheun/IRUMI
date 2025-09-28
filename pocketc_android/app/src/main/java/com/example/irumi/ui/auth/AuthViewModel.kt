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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    var loading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set
    var isLoggedIn by mutableStateOf(!tokenStore.accessToken.isNullOrBlank()); private set

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    /** 앱 시작 시 자동 로그인 조건 확인 */
    fun tryAutoLogin(): Boolean =
        tokenStore.autoLogin && !tokenStore.accessToken.isNullOrBlank()

    fun signUp(name: String, email: String, pw: String, budget: Int, remember: Boolean) =
        launch {
            repo.signUp(SignUpRequest(name, email, pw, budget))
                .onSuccess {
                    Timber.d("!!! 회원가입 성공 remember=${remember} email=${email}")
                    // TODO 로그인 화면으로 돌리기
                    // TODO 200인지도 확인 필요
                    tokenStore.save(it.data!!.accessToken, it.data.refreshToken)
                    tokenStore.autoLogin = remember
                    tokenStore.email = email
                    isLoggedIn = true
                    postAiTransaction()
                }
                .onFailure {
                    Timber.d("!!! 회원가입 실패 ${it}")
                    error = it.message
                    _toastEvent.emit("회원가입에 실패했습니다.")
                }
        }

    fun login(email: String, pw: String, remember: Boolean) =
        launch {
            repo.login(LoginRequest(email, pw))
                .onSuccess { env ->
                    // 로그인 응답이 access 단일 토큰인 스펙
                    tokenStore.save(env.accessToken, env.refreshToken)
                    tokenStore.autoLogin = remember
                    tokenStore.email = email
                    isLoggedIn = true
                }
                .onFailure {
                    Timber.d("로그인 실패 ${it.message}")
                    error = it.message
                    _toastEvent.emit("아이디와 비밀번호를 확인하세요.")
                }
        }

    fun logout() = launch {
        repo.logout()
            .onSuccess {
                tokenStore.clear()
                isLoggedIn = false

            }
            .onFailure {
                Timber.d("!!! 로그아웃 실패 $it")
                error = it.message
                _toastEvent.emit("로그아웃 실패")
            }
    }

    fun reissue() = launch {
        repo.reissue()
            .onSuccess { tokenStore.save(it.accessToken, it.refreshToken) }
            .onFailure { error = it.message }
    }

    fun updateMe(req: AuthEditRequest) = launch {
        repo.updateMe(req).onFailure {
            error = it.message
            _toastEvent.emit("업데이트 실패")
        }
    }

    private fun launch(block: suspend () -> Unit) = viewModelScope.launch {
        loading = true; error = null
        try {
            block()
        } finally {
            loading = false
        }
    }

    fun postAiTransaction() {
        viewModelScope.launch {
            repo.postAiTransaction()
                .onSuccess {
                    Timber.d("!!! postAiTransaction 성공")
                    //_toastEvent.emit("AI 결제 실패")
                }
                .onFailure {
                    error = it.message
                    Timber.d("!!! postAiTransaction 실패 ${it}")
                    //_toastEvent.emit("AI 결제  실패")
                }
        }
    }
}
