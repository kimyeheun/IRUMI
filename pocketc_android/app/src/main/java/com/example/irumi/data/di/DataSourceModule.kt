package com.example.irumi.data.di

import com.example.irumi.data.datasource.DummyDataSource
import com.example.irumi.data.datasource.DummyLocalDataSource
import com.example.irumi.data.datasource.auth.AuthDataSource
import com.example.irumi.data.datasource.auth.AuthRemoteDataSource
import com.example.irumi.data.datasource.events.EventsDataSource
import com.example.irumi.data.datasource.events.EventsRemoteDataSource
import com.example.irumi.data.datasource.main.MainDataSource
import com.example.irumi.data.datasource.main.MainLocalDataSource
import com.example.irumi.data.datasource.payments.PaymentsDataSource
import com.example.irumi.data.datasource.payments.PaymentsRemoteDataSource
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
    abstract fun bindAuthDataSource(authDataSource: AuthRemoteDataSource): AuthDataSource
    // 실서버 붙일 때는 AuthRemoteDataSource 로만 바꾸면 된다.

    @Binds
    @Singleton
    abstract fun bindMainDataSource(mainDataSource: MainLocalDataSource): MainDataSource
    // 실서버 붙일 때는 MainRemoteDataSource 로만 바꾸면 된다.

    @Binds
    @Singleton
    abstract fun bindPaymentsDataSource(paymentsDataSource: PaymentsRemoteDataSource): PaymentsDataSource

    @Binds
    @Singleton
    abstract fun bindEventsDataSource(eventsDataSource: EventsRemoteDataSource): EventsDataSource
}