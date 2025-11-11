package com.example.ottogether.core.model

import java.time.LocalDate

data class BillingInfo(
    val accountMasked: String?,       // "국민은행 00000-0000-0000"
    val loginId: String?,
    val passwordMasked: String?,      // "********"
    val cycleDay: Int,                // 1..31
    val nextBillingDate: LocalDate    // java.time
)