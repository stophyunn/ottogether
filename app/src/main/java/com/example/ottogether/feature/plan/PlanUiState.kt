package com.example.ottogether.feature.plan

import androidx.annotation.DrawableRes
import com.example.ottogether.core.model.Plan

data class PlanUiState(
    val providerId: String = "",
    val ottName: String = "",
    val plans: List<Plan> = emptyList(),
    val selectedPlanId: String? = null,
    @DrawableRes val logoRes: Int? = null
) {
    val selectedPlan: Plan? get() = plans.firstOrNull { it.id == selectedPlanId }
}