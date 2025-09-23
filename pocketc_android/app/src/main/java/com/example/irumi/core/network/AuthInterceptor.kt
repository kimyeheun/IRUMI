package com.example.irumi.core.network

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences // 또는 TokenRepository 등
) : Interceptor {

    companion object {
        private const val AUTH_HEADER_KEY = "Authorization"
        private const val TOKEN_TYPE = "Bearer"
        private const val ACCESS_TOKEN_PREF_KEY = "access_token" // 실제 사용하는 키로 변경
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN_PREF_KEY, null)

        val requestBuilder = originalRequest.newBuilder()
        if (accessToken != null && !originalRequest.url.encodedPath.contains("login") && !originalRequest.url.encodedPath.contains("signup")) {
            // 로그인, 회원가입 요청이 아닐 때만 헤더 추가 (필요에 따라 조건 수정)
            requestBuilder.addHeader(AUTH_HEADER_KEY, "$TOKEN_TYPE $accessToken")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}