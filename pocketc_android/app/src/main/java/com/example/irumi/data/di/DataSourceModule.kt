package com.example.irumi.data.di

import com.example.irumi.data.datasource.DummyDataSource
import com.example.irumi.data.datasource.DummyLocalDataSource
import com.example.irumi.data.datasource.auth.AuthDataSource
import com.example.irumi.data.datasource.auth.AuthLocalDataSource
import com.example.irumi.data.datasource.payments.PaymentsDataSource
import com.example.irumi.data.datasource.payments.PaymentsLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindDummyDataSource(dummyDataSource: DummyLocalDataSource): DummyDataSource

    @Binds
    @Singleton
    abstract fun bindAuthDataSource(authDataSource: AuthLocalDataSource): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindPaymentsDataSource(paymentsDataSource: PaymentsLocalDataSource): PaymentsDataSource
}