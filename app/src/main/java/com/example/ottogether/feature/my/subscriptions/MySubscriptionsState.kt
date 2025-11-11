package com.example.ottogether.feature.my.subscriptions

import com.example.ottogether.core.model.Subscription

// feature/my/subscriptions/MySubscriptionsState.kt
data class MySubscriptionsState(
    val isLoading: Boolean = false,
    val items: List<Subscription> = emptyList(),
    val error: String? = null
)
sealed interface MySubscriptionsEvent {
    data object Refresh : MySubscriptionsEvent
    data class ClickItem(val id: String) : MySubscriptionsEvent
}