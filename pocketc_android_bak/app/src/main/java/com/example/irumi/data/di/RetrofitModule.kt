package com.example.irumi.data.di

import com.example.irumi.BuildConfig
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

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    /**
     * Kotlinx Serialization을 위한 JSON 객체를 제공
     */
    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true // JSON 데이터에 정의되지 않은 키가 있어도 무시하고 파싱
            prettyPrint = true // JSON 문자열 정렬
        }

    /**
     * JSON 데이터를 코틀린 객체로 변환하는 컨버터 팩토리를 제공
     */
    @Provides
    @Singleton
    fun provideJsonConverter(json: Json): Converter.Factory =
        json.asConverterFactory("application/json".toMediaType())

    /**
     * OkHttp에 HTTP 통신 로그를 출력해주는 인터셉터를 제공
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    /**
     * OkHttp 클라이언트 인스턴스를 제공
     */
    @Provides
    @Singleton
    fun provideClient(
        loggingInterceptor: HttpLoggingInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Retrofit 인스턴스를 제공
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        factory: Converter.Factory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client) // OkHttp 클라이언트 연결
            .addConverterFactory(factory) // JSON 컨버터 팩토리 연결
            .build()
}