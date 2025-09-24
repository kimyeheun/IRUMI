package com.example.irumi.data.dto.response.events

import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val event: Event
)
