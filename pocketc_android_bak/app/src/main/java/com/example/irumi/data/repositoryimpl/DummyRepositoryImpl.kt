package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.DummyDataSource
import com.example.irumi.data.mapper.toDummyEntity
import com.example.irumi.domain.entity.DummyEntity
import com.example.irumi.domain.repository.DummyRepository
import javax.inject.Inject

class DummyRepositoryImpl @Inject constructor(
    private val dummyDataSource: DummyDataSource
) : DummyRepository {
    override suspend fun getDummy(page: Int): Result<DummyEntity> =
        runCatching {
            dummyDataSource.getDummy(page).toDummyEntity()
        }
}