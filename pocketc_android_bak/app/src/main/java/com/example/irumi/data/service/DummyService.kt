package com.example.irumi.data.service

import com.example.irumi.data.dto.response.DummyResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DummyService {
    @GET("todos/{page}")
    suspend fun getDummy(
        @Path("page") page: Int
    ): DummyResponse
}