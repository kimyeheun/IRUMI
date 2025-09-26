package com.example.irumi.data.di

import com.example.irumi.data.datasource.DummyDataSource
import com.example.irumi.data.datasource.DummyLocalDataSource
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
}