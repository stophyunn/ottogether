package com.example.ottogether.navigation

// navigation/Routes.kt
sealed class Route(val path: String) {
    data object Home : Route("home")
    data object PlanSelect : Route("plan/{ottId}") {
        fun path(ottId: String) = "plan/$ottId"
    }
    data object MyCalendar : Route("my/calendar")
    data object MyProfile  : Route("my/profile")
    data object MySubscriptions : Route("my/subscriptions")

    data object SubscriptionDetail : Route("my/subscriptions/detail/{ottName}") {
        fun path(ottName: String) = "my/subscriptions/detail/$ottName"
    }

    data object ShareAccount : Route("my/share/{ottName}") {
        fun path(ottName: String) = "my/share/$ottName"
    }

    data object PaymentInfo : Route("payment/info/{ottName}/{plan}") {
        fun path(ottName: String, plan: String) = "payment/info/$ottName/$plan"
    }
}