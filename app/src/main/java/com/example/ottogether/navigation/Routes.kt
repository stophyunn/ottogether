package com.example.ottogether.navigation

import android.net.Uri

// navigation/Routes.kt
sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Login : Route("auth/login")
    data object SignUp : Route("auth/signup")
    data object FindEmail : Route("auth/find-email")
    data object FindPassword : Route("auth/find-password")

    data object Home : Route(BottomNavItem.HOME)
    data object MyCalendar : Route(BottomNavItem.CALENDAR)
    data object MyProfile : Route(BottomNavItem.MYPAGE)

    data object MySubscriptions : Route("my/subscriptions")
    data object SubscriptionDetail : Route("my/subscriptions/{subscriptionId}") {
        fun path(subscriptionId: String) = "my/subscriptions/${Uri.encode(subscriptionId)}"
    }
    data object Account : Route("my/account")

    data object PlanSelect : Route("plan/{providerId}") {
        fun path(providerId: String) = "plan/${Uri.encode(providerId)}"
    }

    data object HostShare : Route("plan/{providerId}/host/{planId}") {
        fun path(providerId: String, planId: String) =
            "plan/${Uri.encode(providerId)}/host/${Uri.encode(planId)}"
    }

    data object MemberPayment : Route("plan/{providerId}/member/{planId}") {
        fun path(providerId: String, planId: String) =
            "plan/${Uri.encode(providerId)}/member/${Uri.encode(planId)}"
    }
}