package com.example.irumi.ui.home.model

import com.example.irumi.ui.home.component.UiStatus

data class TodoItemUi(
    val id: Int,
    val title: String,
    val status: UiStatus,
    val dimAlpha: Float,
    val type: Int, // 0: Daily, 1: Weekly, 2: Monthly
    val template: String,
    val progress: Int,
    val value: Int
)