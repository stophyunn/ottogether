package com.example.ottogether.core.model

import java.time.LocalDate

data class Party(
    val id: String,
    val providerId: String,     // ex) "netflix"
    val planId: String,         // ex) "premium"
    val ownerId: String,        // 파티장 user id
    val members: List<String>,  // 참여자 user id
    val inviteCode: String,     // 초대코드
    val nextBillingDate: LocalDate
)