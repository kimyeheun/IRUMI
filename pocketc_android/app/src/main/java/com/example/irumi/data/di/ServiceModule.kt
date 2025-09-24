package com.example.irumi.data.di

import com.example.irumi.data.service.AuthService
import com.example.irumi.data.service.DummyService
import com.example.irumi.data.service.EventsService
import com.example.irumi.data.service.MainService
import com.example.irumi.data.service.PaymentsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun bindDummyService(retrofit: Retrofit): DummyService =
        retrofit.create(DummyService::class.java)

    @Provides
    @Singleton
    fun bindAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun bindMainService(retrofit: Retrofit): MainService =
        retrofit.create(MainService::class.java)

    @Provides
    @Singleton
    fun bindPaymentsService(retrofit: Retrofit): PaymentsService =
        retrofit.create(PaymentsService::class.java)

    @Provides
    @Singleton
    fun bindEventsService(retrofit: Retrofit): EventsService =
        retrofit.create(EventsService::class.java)
}