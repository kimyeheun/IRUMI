package com.example.irumi.data.mapper

import com.example.irumi.data.dto.response.DummyResponse
import com.example.irumi.domain.entity.DummyEntity

fun DummyResponse.toDummyEntity() =
    DummyEntity(
        userId = userId,
        title = title
    )