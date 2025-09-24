package com.example.irumi.data.di

import com.example.irumi.data.repositoryimpl.AuthRepositoryImpl
import com.example.irumi.data.repositoryimpl.DummyRepositoryImpl
import com.example.irumi.data.repositoryimpl.EventsRepositoryImpl
import com.example.irumi.data.repositoryimpl.MainRepositoryImpl
import com.example.irumi.data.repositoryimpl.PaymentsRepositoryImpl
import com.example.irumi.domain.repository.AuthRepository
import com.example.irumi.domain.repository.DummyRepository
import com.example.irumi.domain.repository.EventsRepository
import com.example.irumi.domain.repository.MainRepository
import com.example.irumi.domain.repository.PaymentsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDummyRepository(dummyRepositoryImpl: DummyRepositoryImpl): DummyRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMainRepository(mainRepositoryImpl: MainRepositoryImpl): MainRepository

    @Binds
    @Singleton
    abstract fun bindPaymentsRepository(paymentsRepositoryImpl: PaymentsRepositoryImpl): PaymentsRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(eventRepositoryImpl: EventsRepositoryImpl): EventsRepository
}