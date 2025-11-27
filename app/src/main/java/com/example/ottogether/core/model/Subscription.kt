package com.example.ottogether.core.model

import java.time.LocalDate

data class Subscription(
    val id: String,
    val provider: Provider,
    val plan: Plan,
    val ownerUserId: String,         // 파티장
    val members: List<String>,       // user ids
    val billing: BillingInfo,
    val pendingExits: Map<String, LocalDate> = emptyMap(),
)