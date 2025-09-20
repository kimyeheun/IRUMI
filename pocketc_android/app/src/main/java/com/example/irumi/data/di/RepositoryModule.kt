package com.example.irumi.data.di

import com.example.irumi.data.repositoryimpl.DummyRepositoryImpl
import com.example.irumi.data.repositoryimpl.PaymentsRepositoryImpl
import com.example.irumi.domain.repository.DummyRepository
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
    abstract fun bindPaymentsRepository(paymentsRepositoryImpl: PaymentsRepositoryImpl): PaymentsRepository
}