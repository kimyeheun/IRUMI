package com.example.irumi.data.di

import android.content.SharedPreferences
import com.example.irumi.BuildConfig
import com.example.irumi.core.network.AuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton


/**
 * Hilt를 사용하여 네트워크 통신에 필요한 모든 객체(JSON, OkHttp, Retrofit)를 생성하고 제공하는 모듈
 *
 * 1. Hilt는 Retrofit이 필요할 때, 먼저 OkHttpClient와 Converter.Factory를 찾습니다.
 * 2. OkHttpClient를 만들기 위해 HttpLoggingInterceptor를 찾습니다.
 * 3. Converter.Factory를 만들기 위해 Json 객체를 찾습니다.
 * 4. 모든 의존성을 찾고 생성한 후, 최종적으로 Retrofit 객체를 완성하여 필요한 곳에 주입
 */

/**
 * Hilt 네트워크 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    /** Kotlinx Serialization JSON */
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    /** Converter.Factory */
    @Provides
    @Singleton
    fun provideJsonConverter(json: Json): Converter.Factory =
        json.asConverterFactory("application/json".toMediaType())

    /** Logging Interceptor */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    /** AuthInterceptor */
    @Provides
    @Singleton
    fun provideAuthInterceptor(prefs: SharedPreferences): AuthInterceptor =
        AuthInterceptor(prefs)

    /** 단일 OkHttpClient (중복 제거) */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        // Auth를 먼저 추가하면 Logging에서 Authorization 헤더까지 볼 수 있음
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    /** Retrofit */
    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        factory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(factory)
        .build()
}