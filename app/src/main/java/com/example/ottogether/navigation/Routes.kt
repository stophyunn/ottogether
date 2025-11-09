package com.example.ottogether.navigation

sealed class Route(val path: String) {
    // 메인
    data object Home : Route("home")

    // 요금제 선택 (홈 → 요금제)
    data object PlanSelect : Route("plan/{ottId}") {
        fun path(ottId: String) = "plan/$ottId"
    }

    // 마이 탭 하위
    data object MyCalendar : Route("my/calendar")
    data object MyProfile  : Route("my/profile")
    data object MySubscriptions : Route("my/subscriptions")

    // 마이 상세 (내 계정 공유하기)
    data object ShareAccount : Route("my/share/{ottName}") {
        fun path(ottName: String) = "my/share/$ottName"
    }

    // 결제 정보
    data object PaymentInfo : Route("payment/info/{ottName}/{plan}") {
        fun path(ottName: String, plan: String) = "payment/info/$ottName/$plan"
    }
}