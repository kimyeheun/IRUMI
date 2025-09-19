package com.example.irumi.domain.repository

import com.example.irumi.domain.entity.DummyEntity

interface DummyRepository {
    suspend fun getDummy(page: Int): Result<DummyEntity>
}