package com.example.irumi.data.di

import com.example.irumi.data.service.DummyService
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
}