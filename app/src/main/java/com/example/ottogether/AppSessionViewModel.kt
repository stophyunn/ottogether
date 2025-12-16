package com.example.ottogether

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ottogether.R
import com.example.ottogether.core.data.SeedData
import com.example.ottogether.core.data.SubscriptionRepository
import com.example.ottogether.core.data.UserRepository
import com.example.ottogether.core.model.AuthResult
import com.example.ottogether.core.model.Plan
import com.example.ottogether.core.model.Provider
import com.example.ottogether.core.model.Subscription
import com.example.ottogether.core.model.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltViewModel
class AppSessionViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    private val seedData: SeedData,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(
        AppSessionState(
            catalogs = seedData.catalogs
        )
    )
    val state: StateFlow<AppSessionState> = _state.asStateFlow()

    private val profileImages = listOf(R.drawable.profile)

    init {
        viewModelScope.launch {
            refreshUsers()
            auth.currentUser?.let { firebaseUser ->
                ensureUserProfile(firebaseUser)?.let { setLoggedInUser(it) }
            }
        }
    }

    fun onSplashFinished() {
        _state.update { it.copy(hasSeenSplash = true) }
    }

    suspend fun loginWithCredentials(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = result.user ?: return AuthResult(false, "로그인에 실패했어요")
            val profile = ensureUserProfile(firebaseUser)
                ?: return AuthResult(false, "프로필을 불러오지 못했어요")
            setLoggedInUser(profile)
            AuthResult(success = true)
        } catch (e: Exception) {
            AuthResult(success = false, message = e.localizedMessage ?: "로그인에 실패했어요")
        }
    }

    suspend fun registerUser(name: String, email: String, password: String, phone: String?): AuthResult {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return AuthResult(success = false, message = "필수 정보를 입력해주세요")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return AuthResult(success = false, message = "올바른 이메일 형식을 입력해주세요")
        }
        if (password.length < 6) {
            return AuthResult(success = false, message = "비밀번호는 6자 이상이어야 해요")
        }

        return try {
            auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = auth.currentUser ?: return AuthResult(false, "회원가입에 실패했어요")
            val user = User(
                id = firebaseUser.uid,
                name = name.trim(),
                email = email.trim(),
                phone = phone?.trim()?.takeIf { it.isNotBlank() },
                profileImageRes = profileImages.firstOrNull(),
                password = null
            )
            userRepository.addUser(user)
            refreshUsers()
            setLoggedInUser(user)
            AuthResult(success = true)
        } catch (e: Exception) {
            AuthResult(success = false, message = e.localizedMessage ?: "회원가입에 실패했어요")
        }
    }

    fun logout() {
        auth.signOut()
        _state.update {
            it.copy(
                currentUser = null,
                subscriptions = emptyList(),
                selectedCalendarDate = LocalDate.now()
            )
        }
    }

    fun withdrawAccount(onComplete: (Boolean) -> Unit = {}) {
        val user = _state.value.currentUser ?: run {
            onComplete(false)
            return
        }
        viewModelScope.launch {
            val success = runCatching {
                val subscriptions = repository.getMySubscriptions(user.id)
                subscriptions.forEach { subscription ->
                    if (subscription.ownerUserId == user.id) {
                        repository.deleteSubscription(subscription.id)
                    } else {
                        repository.leaveSubscription(subscription.id, user.id)
                    }
                }
                userRepository.deleteUser(user.id)
                try {
                    auth.currentUser?.delete()?.await()
                } catch (_: Exception) {
                    // ignore best-effort auth deletion failures
                }
                true
            }.getOrElse { false }

            logout()
            onComplete(success)
        }
    }

    fun leaveSubscription(id: String) {
        val user = _state.value.currentUser ?: return
        viewModelScope.launch {
            repository.leaveSubscription(id, user.id)
            refreshSubscriptions(user.id)
        }
    }

    suspend fun leaveAsOwner(subscriptionId: String, transferTo: String?): AuthResult {
        val user = _state.value.currentUser ?: return AuthResult(false, "로그인이 필요해요")
        val current = repository.getSubscription(subscriptionId)
        if (current.ownerUserId != user.id) {
            return AuthResult(false, "파티장만 사용할 수 있어요")
        }

        return if (current.members.isEmpty()) {
            repository.deleteSubscription(subscriptionId)
            refreshSubscriptions(user.id)
            AuthResult(true, "파티방을 삭제했어요")
        } else {
            val newOwnerId = transferTo ?: return AuthResult(false, "파티장을 양도할 파티원을 선택해주세요")
            if (newOwnerId !in current.members) {
                return AuthResult(false, "선택한 파티원을 찾을 수 없어요")
            }
            repository.transferOwnership(subscriptionId, newOwnerId)
                ?: return AuthResult(false, "파티장을 양도하지 못했어요")
            repository.leaveSubscription(subscriptionId, user.id)
            refreshSubscriptions(user.id)
            AuthResult(true, "파티장을 넘기고 파티에서 나갔어요")
        }
    }

    fun scheduleLeaveSubscription(id: String, leaveDate: LocalDate) {
        val user = _state.value.currentUser ?: return
        viewModelScope.launch {
            repository.scheduleLeave(id, user.id, leaveDate)
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

    suspend fun getRecommendedParties(provider: Provider, planId: String?): List<Subscription> {
        val userId = _state.value.currentUser?.id
        return repository.getRecommendedParties(provider, planId, userId)
    }

    suspend fun hostNewSubscription(
        provider: Provider,
        plan: Plan,
        loginId: String,
        password: String,
        account: String,
        firstBillingDate: LocalDate
    ): AuthResult {
        val user = _state.value.currentUser ?: return AuthResult(false, "로그인이 필요해요")
        return try {
            repository.createHostedSubscription(
                ownerId = user.id,
                provider = provider,
                plan = plan,
                accountMasked = account.takeIf { it.isNotBlank() },
                loginId = loginId.takeIf { it.isNotBlank() },
                passwordMasked = password.takeIf { it.isNotBlank() },
                firstBillingDate = firstBillingDate
            )
            val updated = repository.getMySubscriptions(user.id)
            _state.update { it.copy(subscriptions = updated) }
            AuthResult(true, "파티 등록을 완료했어요")
        } catch (e: Exception) {
            AuthResult(false, e.localizedMessage ?: "파티를 등록하지 못했어요")
        }
    }

    fun attachSubscription(subscription: Subscription) {
        _state.update { state ->
            if (state.subscriptions.any { it.id == subscription.id }) state else {
                state.copy(subscriptions = state.subscriptions + subscription)
            }
        }
    }

    fun cycleProfileImage() {
        val user = _state.value.currentUser ?: return
        val options = profileImages.ifEmpty { listOf(R.drawable.profile) }
        val currentIndex = options.indexOf(user.profileImageRes).takeIf { it >= 0 } ?: 0
        val nextRes = options[(currentIndex + 1) % options.size]
        persistUser(user.copy(profileImageRes = nextRes, profileImageUri = null))
    }

    fun updateProfileImage(uri: String?) {
        val user = _state.value.currentUser ?: return
        persistUser(user.copy(profileImageUri = uri, profileImageRes = null))
    }

    fun updateCurrentUserName(newName: String) {
        val user = _state.value.currentUser ?: return
        val trimmed = newName.trim()
        if (trimmed.isBlank()) return
        persistUser(user.copy(name = trimmed))
    }

    fun updateCurrentUserEmail(newEmail: String) {
        val user = _state.value.currentUser ?: return
        val trimmed = newEmail.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            try {
                auth.currentUser?.updateEmail(trimmed)?.await()
            } catch (_: Exception) {
                // no-op: we still persist locally to simulate server update
            }
            persistUser(user.copy(email = trimmed))
        }
    }

    fun updateCurrentUserPhone(newPhone: String) {
        val user = _state.value.currentUser ?: return
        val trimmed = newPhone.trim()
        if (trimmed.isBlank()) return
        persistUser(user.copy(phone = trimmed))
    }

    fun updateCurrentUserAccount(newAccount: String) {
        val user = _state.value.currentUser ?: return
        val trimmed = newAccount.trim()
        if (trimmed.isBlank()) return
        persistUser(user.copy(accountNumber = trimmed))
    }

    fun updateCurrentUserPassword(newPassword: String) {
        val user = _state.value.currentUser ?: return
        if (newPassword.isBlank()) return
        viewModelScope.launch {
            try {
                auth.currentUser?.updatePassword(newPassword)?.await()
            } catch (_: Exception) {
                // ignore; mimic best-effort update
            }
            persistUser(user.copy(password = newPassword))
        }
    }

    suspend fun joinPartyByCode(rawCode: String): AuthResult {
        val user = _state.value.currentUser ?: return AuthResult(false, "로그인이 필요해요")
        val trimmed = rawCode.trim()
        if (trimmed.isBlank()) {
            return AuthResult(success = false, message = "파티방 주소를 입력해주세요")
        }
        return try {
            val normalized = normalizeInviteCode(trimmed)
            val result = repository.joinPartyByCode(normalized, user.id)
                ?: return AuthResult(false, "유효하지 않은 주소이거나 이미 참여했어요")
            val updatedSubs = repository.getMySubscriptions(user.id)
            _state.update { it.copy(subscriptions = updatedSubs) }
            AuthResult(true, "파티에 참여했어요!")
        } catch (e: Exception) {
            AuthResult(false, e.localizedMessage ?: "파티에 참여하지 못했어요")
        }
    }

    suspend fun transferOwnership(subscriptionId: String, newOwnerId: String): AuthResult {
        val user = _state.value.currentUser ?: return AuthResult(false, "로그인이 필요해요")
        val current = repository.getSubscription(subscriptionId)
        if (current.ownerUserId != user.id) {
            return AuthResult(false, "파티장만 사용할 수 있어요")
        }
        if (newOwnerId == user.id) {
            return AuthResult(false, "이미 파티장이에요")
        }
        val updated = repository.transferOwnership(subscriptionId, newOwnerId)
            ?: return AuthResult(false, "선택한 파티원을 찾을 수 없어요")
        val updatedSubs = repository.getMySubscriptions(user.id)
        _state.update { it.copy(subscriptions = updatedSubs) }
        val name = _state.value.users.firstOrNull { it.id == newOwnerId }?.name
        return AuthResult(true, name?.let { "$it 님에게 파티장을 넘겼어요" } ?: "파티장을 양도했어요")
    }

    private fun normalizeInviteCode(raw: String): String {
        val trimmed = raw.trim()
        return trimmed.substringAfterLast('/')
    }

    private suspend fun setLoggedInUser(user: User) {
        _state.update { it.copy(isLoading = true) }
        val subs = repository.getMySubscriptions(user.id)
        refreshUsers()
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

    private suspend fun refreshUsers() {
        val users = runCatching { userRepository.getUsers() }
            .getOrElse { seedData.users }
        _state.update { it.copy(users = users) }
    }

    private fun persistUser(updated: User) {
        viewModelScope.launch {
            userRepository.updateUser(updated)
            refreshUsers()
            _state.update { it.copy(currentUser = updated) }
        }
    }

    private suspend fun ensureUserProfile(
        firebaseUser: com.google.firebase.auth.FirebaseUser,
        fallbackName: String? = null
    ): User? {
        val existingById = userRepository.getUserById(firebaseUser.uid)
        val email = firebaseUser.email
        val existingByEmail = email?.let { userRepository.findByEmail(it) }
        val resolved = existingById ?: existingByEmail ?: User(
            id = firebaseUser.uid,
            name = fallbackName ?: firebaseUser.displayName ?: email ?: "사용자",
            email = email,
            phone = null,
            profileImageRes = profileImages.firstOrNull(),
            password = null
        )
        if (existingById == null) {
            userRepository.addUser(resolved)
        }
        return resolved
    }
}
