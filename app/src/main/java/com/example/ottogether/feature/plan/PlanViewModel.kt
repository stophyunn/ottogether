package com.example.ottogether.feature.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ottogether.core.data.SeedData
import com.example.ottogether.core.model.Provider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val seedData: SeedData
) : ViewModel() {

    private val _ui = MutableStateFlow(PlanUiState())
    val ui: StateFlow<PlanUiState> = _ui.asStateFlow()

    /** 홈에서 넘어온 ottId(예: "NETFLIX" or "넷플릭스") 기준으로 요금제 목록 로드 */
    fun loadPlans(ottId: String) = viewModelScope.launch {
        val provider = runCatching { Provider.valueOf(ottId.uppercase()) }.getOrNull()
        val catalog = provider?.let { seedData.catalog(it) }
            ?: seedData.catalogs.firstOrNull { it.displayName == ottId }
            ?: return@launch

        _ui.update {
            it.copy(
                providerId = catalog.provider.name,
                ottName = catalog.displayName,
                plans = catalog.plans,
                selectedPlanId = catalog.plans.firstOrNull()?.id,
                logoRes = catalog.logoRes
            )
        }
    }

    /** 요금제 선택 변경 */
    fun selectPlan(planId: String) {
        _ui.update { it.copy(selectedPlanId = planId) }
    }
}