package com.example.irumi.ui.auth

import kotlinx.coroutines.delay
import java.util.UUID

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val nickname: String,
    // 여기는 아직!
    val profileImageUrl: String? = null,
    val budget: Int = 0
)

data class Token(
    val accessToken: String,
    val refreshToken: String
)

sealed class SignUpResult {
    data class Success(val token: Token) : SignUpResult()
    data class Error(val status: Int, val message: String) : SignUpResult()
}

/**
 * 백엔드 준비 전까지 회원가입/로그인을 로컬로 흉내내는 Mock.
 */
object AuthMockRepository {

    // 아주 단순한 중복 이메일 체크 예시
    private val usedEmails = mutableSetOf<String>()

    suspend fun signUp(req: SignUpRequest): SignUpResult {
        delay(600) // 네트워크 흉내

        // 유효성 간단 체크
        if (req.name.isBlank() || req.email.isBlank() ||
            req.password.isBlank() || req.nickname.isBlank() || req.budget <= 0
        ) {
            return SignUpResult.Error(400, "오류오류")
        }

        if (usedEmails.contains(req.email.lowercase())) {
            return SignUpResult.Error(409, "이미 존재하는 멤버입니다.")
        }

        // 성공 처리
        usedEmails.add(req.email.lowercase())
        val token = Token(
            accessToken = "acc-${UUID.randomUUID()}",
            refreshToken = "ref-${UUID.randomUUID()}"
        )
        return SignUpResult.Success(token)
    }

    /**
     * 임시 로그인: 아이디/비밀번호 모두 "1234"인 경우만 성공
     */
    suspend fun login(id: String, password: String): Boolean {
        delay(400)
        return id == "1234" && password == "1234"
    }
}
