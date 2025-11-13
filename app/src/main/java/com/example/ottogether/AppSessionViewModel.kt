package com.example.ottogether

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ottogether.core.data.SeedData
import com.example.ottogether.core.data.SubscriptionRepository
import com.example.ottogether.core.data.UserRepository
import com.example.ottogether.core.model.AuthResult
import com.example.ottogether.core.model.Subscription
import com.example.ottogether.core.model.User
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
    private val seedData: SeedData,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        AppSessionState(
            catalogs = seedData.catalogs,
            users = userRepository.getUsers()
        )
    )
    val state: StateFlow<AppSessionState> = _state.asStateFlow()

    fun onSplashFinished() {
        _state.update { it.copy(hasSeenSplash = true) }
    }

    private fun loginById(userId: String) {
        val user = userRepository.getUsers().firstOrNull { it.id == userId } ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val subs = repository.getMySubscriptions(user.id)
            _state.update {
                it.copy(
                    isLoading = false,
                    currentUser = user,
                    subscriptions = subs,
                    selectedCalendarDate = subs.minByOrNull { sub -> sub.billing.nextBillingDate }
                        ?.billing?.nextBillingDate ?: LocalDate.now()
                )
            }
        }
    }

    fun loginWithCredentials(email: String, password: String): AuthResult {
        val user = userRepository.findByEmail(email.trim())
            ?: return AuthResult(success = false, message = "가입된 이메일을 찾을 수 없어요")
        if (user.password != password) {
            return AuthResult(success = false, message = "비밀번호가 올바르지 않아요")
        }
        loginById(user.id)
        return AuthResult(success = true)
    }

    fun loginWithTestAccount(): AuthResult {
        val testUser = seedData.users.firstOrNull()
            ?: return AuthResult(success = false, message = "테스트 계정을 찾을 수 없어요")
        loginById(testUser.id)
        return AuthResult(success = true)
    }

    fun registerUser(name: String, email: String, password: String, phone: String?): AuthResult {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return AuthResult(success = false, message = "필수 정보를 입력해주세요")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return AuthResult(success = false, message = "올바른 이메일 형식을 입력해주세요")
        }
        if (password.length < 6) {
            return AuthResult(success = false, message = "비밀번호는 6자 이상이어야 해요")
        }
        if (userRepository.findByEmail(email.trim()) != null) {
            return AuthResult(success = false, message = "이미 가입된 이메일이에요")
        }

        val user = User(
            id = generateUserId(),
            name = name.trim(),
            email = email.trim(),
            phone = phone?.trim()?.takeIf { it.isNotBlank() },
            password = password
        )
        userRepository.addUser(user)
        _state.update { it.copy(users = userRepository.getUsers()) }
        return AuthResult(success = true)
    }

    fun logout() {
        _state.update {
            it.copy(
                currentUser = null,
                subscriptions = emptyList(),
                selectedCalendarDate = LocalDate.now()
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
            _state.update {
                it.copy(
                    subscriptions = subs,
                    selectedCalendarDate = it.selectedCalendarDate
                )
            }
        }
    }

    fun selectCalendarDate(date: LocalDate) {
        _state.update { it.copy(selectedCalendarDate = date) }
    }

    fun updateNextBillingDate(subscriptionId: String, date: LocalDate) {
        val userId = _state.value.currentUser?.id ?: return
        viewModelScope.launch {
            repository.updateNextBillingDate(subscriptionId, date)
            val updated = repository.getMySubscriptions(userId)
            _state.update {
                it.copy(
                    subscriptions = updated,
                    selectedCalendarDate = date
                )
            }
        }
    }

    fun attachSubscription(subscription: Subscription) {
        _state.update { state ->
            if (state.subscriptions.any { it.id == subscription.id }) state else {
                state.copy(subscriptions = state.subscriptions + subscription)
            }
        }
    }

    private fun generateUserId(): String = "u" + System.currentTimeMillis().toString(16)
}
