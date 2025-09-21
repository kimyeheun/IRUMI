package com.example.irumi.core.navigation

import kotlinx.serialization.Serializable

interface Route

@Serializable
object Home : Route

@Serializable
object Payments : Route

@Serializable
object Stats : Route

@Serializable
object Events : Route

@Serializable
data class PaymentDetail(val paymentId: Int) : Route
