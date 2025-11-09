package com.example.ottogether.feature.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    // 추후 실제 데이터 연동 시 주입해서 사용
    // private val repo: PlanRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(PlanUiState())
    val ui: StateFlow<PlanUiState> = _ui.asStateFlow()

    /** 홈에서 넘어온 ottId(예: "넷플릭스") 기준으로 요금제 목록 로드 */
    fun loadPlans(ottId: String) = viewModelScope.launch {
        // TODO: repo에서 받아오도록 교체
        val plans = listOf("프리미엄", "스탠다드", "베이식", "광고형 스탠다드")

        _ui.update {
            it.copy(
                ottName = ottId,
                plans = plans,
                selectedPlan = plans.first() // 기본 선택: 프리미엄
            )
        }
    }

    /** 요금제 선택 변경 */
    fun selectPlan(plan: String) {
        _ui.update { it.copy(selectedPlan = plan) }
    }
}