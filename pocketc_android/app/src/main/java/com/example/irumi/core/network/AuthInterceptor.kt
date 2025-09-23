package com.example.irumi.core.network

import com.example.irumi.core.pref.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore // 또는 TokenRepository 등
) : Interceptor {

    companion object {
        private const val AUTH_HEADER_KEY = "Authorization"
        private const val TOKEN_TYPE = "Bearer"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenStore.accessToken

        val requestBuilder = originalRequest.newBuilder()
        val path = originalRequest.url.encodedPath
        val shouldAddToken = accessToken != null &&
                !path.contains("/login") && // 실제 API 경로에 맞게 수정
                !path.contains("/signup") && // 실제 API 경로에 맞게 수정
                !path.contains("/reissue") // 토큰 재발급 경로에도 토큰 추가 X (필요시)


        if (shouldAddToken) {
            requestBuilder.addHeader(AUTH_HEADER_KEY, "$TOKEN_TYPE $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}