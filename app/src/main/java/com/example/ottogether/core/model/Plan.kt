package com.example.ottogether.core.model

data class Plan(
    val id: String,
    val name: String,        // "프리미엄"
    val quality: String,     // "가장 좋음"
    val resolution: String,  // "4K + HDR"
    val maxScreens: Int,     // 6
    val monthlyPrice: Money
)
