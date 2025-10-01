package com.example.irumi.core.util.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toPaymentDate(): String {
    val odt = LocalDateTime.parse(this)
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH:mm", Locale.KOREAN)
    return odt.format(formatter)
}