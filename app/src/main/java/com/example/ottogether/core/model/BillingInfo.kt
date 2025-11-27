package com.example.ottogether.core.model

import java.time.LocalDate

data class BillingInfo(
    val accountMasked: String? = null,
    val loginId: String? = null,
    val passwordMasked: String? = null,
    val cycleDay: Int = 1,
    val nextBillingDate: LocalDate = LocalDate.now()
)