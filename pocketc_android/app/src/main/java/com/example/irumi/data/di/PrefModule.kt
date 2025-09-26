package com.example.irumi.data.di

import com.example.irumi.core.pref.SharedPrefsTokenStore
import com.example.irumi.core.pref.TokenStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrefModule {
    @Binds
    @Singleton
    abstract fun bindTokenStore(impl: SharedPrefsTokenStore): TokenStore
}
