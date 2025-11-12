package com.example.ottogether.navigation

sealed class Route(val path: String) {
    // Entry
    data object Splash : Route("splash")
    data object Login  : Route("auth/login")
    data object Signup : Route("auth/signup")

    // 메인 탭
    data object Home : Route("home")

    // 마이 탭 하위
    data object MyCalendar      : Route("my/calendar")
    data object MyProfile       : Route("my/profile")
    data object MySubscriptions : Route("my/subscriptions")

    // 홈 -> 요금제 선택 (Home에서 선택한 OTT 식별자)
    data object PlanSelect : Route("plan/{ottId}") {
        fun path(ottId: String) = "plan/$ottId"
    }

    // 파티(파티장/파티원 플로우 공통)
    data object PartyCreate : Route("party/create/{providerId}/{planId}") {
        fun path(providerId: String, planId: String) = "party/create/$providerId/$planId"
    }
    data object PartyDetail : Route("party/detail/{partyId}") {
        fun path(partyId: String) = "party/detail/$partyId"
    }
    data object PartyEdit : Route("party/edit/{partyId}") {
        fun path(partyId: String) = "party/edit/$partyId"
    }
    data object PartyJoin : Route("party/join")

    // 구독 상세 (필요 시 사용)
    data object SubscriptionDetail : Route("subscription/{partyId}") {
        fun path(partyId: String) = "subscription/$partyId"
    }
}