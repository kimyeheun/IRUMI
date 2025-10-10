package com.example.irumi.data.datasource

import com.example.irumi.data.dto.response.DummyResponse
import com.example.irumi.data.service.DummyService
import javax.inject.Inject

class DummyRemoteDataSource @Inject constructor(
    private val dummyService: DummyService
) : DummyDataSource {
    override suspend fun getDummy(page: Int): DummyResponse = dummyService.getDummy(page)

}