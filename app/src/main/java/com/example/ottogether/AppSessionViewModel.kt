package com.example.ottogether

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ottogether.core.data.SeedData
import com.example.ottogether.core.data.SubscriptionRepository
import com.example.ottogether.core.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AppSessionViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    private val seedData: SeedData
) : ViewModel() {

    private val _state = MutableStateFlow(
        AppSessionState(
            catalogs = seedData.catalogs,
            users = seedData.users
        )
    )
    val state: StateFlow<AppSessionState> = _state.asStateFlow()

    fun onSplashFinished() {
        _state.update { it.copy(hasSeenSplash = true) }
    }

    fun loginDefault() {
        if (_state.value.currentUser != null) return
        val user = seedData.users.first()
        login(user.id)
    }

    fun login(userId: String) {
        val user = seedData.users.firstOrNull { it.id == userId } ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val subs = repository.getMySubscriptions(user.id)
            _state.update {
                it.copy(
                    isLoading = false,
                    currentUser = user,
                    subscriptions = subs
                )
            }
        }
    }

    fun logout() {
        _state.update {
            it.copy(
                currentUser = null,
                subscriptions = emptyList()
            )
        }
    }

    fun leaveSubscription(id: String) {
        val user = _state.value.currentUser ?: return
        viewModelScope.launch {
            repository.leaveSubscription(id, user.id)
            refreshSubscriptions(user.id)
        }
    }

    fun refreshSubscriptions(userId: String? = _state.value.currentUser?.id) {
        val id = userId ?: return
        viewModelScope.launch {
            val subs = repository.getMySubscriptions(id)
            _state.update { it.copy(subscriptions = subs) }
        }
    }

    fun selectCalendarDate(date: LocalDate) {
        _state.update { it.copy(selectedCalendarDate = date) }
    }

    fun updateNextBillingDate(subscriptionId: String, date: LocalDate) {
        _state.update { state ->
            state.copy(
                subscriptions = state.subscriptions.map { sub ->
                    if (sub.id == subscriptionId) {
                        sub.copy(
                            billing = sub.billing.copy(
                                cycleDay = date.dayOfMonth,
                                nextBillingDate = date
                            )
                        )
                    } else {
                        sub
                    }
                }
            )
        }
    }

    fun attachSubscription(subscription: Subscription) {
        _state.update { state ->
            if (state.subscriptions.any { it.id == subscription.id }) state else {
                state.copy(subscriptions = state.subscriptions + subscription)
            }
        }
    }
}
