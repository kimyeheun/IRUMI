package com.example.irumi.domain.entity


data class BaseEntity<T> (
    val status: Int,
    val message: String,
    val data: T? = null
)