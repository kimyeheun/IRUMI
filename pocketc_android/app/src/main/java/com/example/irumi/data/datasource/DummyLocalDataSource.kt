package com.example.irumi.data.datasource

import com.example.irumi.data.dto.response.DummyResponse
import javax.inject.Inject

class DummyLocalDataSource @Inject constructor() : DummyDataSource {
    override suspend fun getDummy(page: Int): DummyResponse {
        return DummyResponse(
            userId = 111,
            id = page,
            title = "로컬 더미 데이터",
            completed = true
        )
    }
}