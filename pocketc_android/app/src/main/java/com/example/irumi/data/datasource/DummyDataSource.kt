package com.example.irumi.data.datasource

import com.example.irumi.data.dto.response.DummyResponse

interface DummyDataSource {
    suspend fun getDummy(page: Int): DummyResponse
}