package com.example.irumi.data.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences =
        context.getSharedPreferences("irumi_prefs", Context.MODE_PRIVATE)

    // 선택: 에디터도 주입받고 싶다면
    @Provides
    fun provideSharedPrefsEditor(prefs: SharedPreferences): SharedPreferences.Editor =
        prefs.edit()
}