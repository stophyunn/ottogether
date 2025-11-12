package com.example.ottogether

import com.example.ottogether.core.data.SeedData
import com.example.ottogether.core.model.Subscription
import com.example.ottogether.core.model.User
import java.time.LocalDate

data class AppSessionState(
    val isLoading: Boolean = false,
    val hasSeenSplash: Boolean = false,
    val currentUser: User? = null,
    val catalogs: List<SeedData.ProviderCatalog> = emptyList(),
    val users: List<User> = emptyList(),
    val subscriptions: List<Subscription> = emptyList(),
    val selectedCalendarDate: LocalDate? = null
) {
    val isLoggedIn: Boolean get() = currentUser != null
}
