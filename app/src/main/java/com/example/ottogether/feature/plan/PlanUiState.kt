package com.example.ottogether.feature.plan

data class PlanUiState(
    val ottName: String = "",
    val plans: List<String> = emptyList(),
    val selectedPlan: String? = null
)