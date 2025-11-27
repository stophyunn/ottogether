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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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

    suspend fun loginWithTestAccount(): AuthResult {
        val seedUser = seedData.users.firstOrNull()
        val email = seedUser?.email ?: "test@ottogether.app"
        val password = seedUser?.password ?: "password1"
        return try {
            val methods = auth.fetchSignInMethodsForEmail(email).await()
            if (methods.signInMethods.isNullOrEmpty()) {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                } catch (collision: FirebaseAuthUserCollisionException) {
                    // Account already exists: fall back to sign-in below
                }
            }
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return AuthResult(false, "테스트 계정을 찾을 수 없어요")
            val profile = ensureUserProfile(firebaseUser, fallbackName = seedUser?.name)
                ?: return AuthResult(false, "프로필을 만들 수 없어요")
            setLoggedInUser(profile)
            AuthResult(true, "테스트 계정으로 로그인했어요")
        } catch (e: Exception) {
            if (e.message?.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) == true) {
                return loginWithSeedDataFallback(seedUser)
            }
            AuthResult(false, "테스트 계정 로그인 실패: ${e.message}")
        }
    }

    private suspend fun loginWithSeedDataFallback(seedUser: User?): AuthResult {
        val fallbackUser = seedUser ?: User(
            id = "seed-test-user",
            name = "테스트 사용자",
            email = null,
            phone = null,
            profileImageRes = profileImages.firstOrNull(),
            password = null
        )
        setLoggedInUserFromSeed(fallbackUser)
        return AuthResult(true, "테스트 계정을 로컬 데이터로 불러왔어요")
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

    fun leaveSubscription(id: String) {
        val user = _state.value.currentUser ?: return
        viewModelScope.launch {
            repository.leaveSubscription(id, user.id)
            refreshSubscriptions(user.id)
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
    ) {
        val user = _state.value.currentUser ?: return
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
        val normalized = normalizeInviteCode(trimmed)
        val result = repository.joinPartyByCode(normalized, user.id)
            ?: return AuthResult(false, "유효하지 않은 주소이거나 이미 참여했어요")
        val updatedSubs = repository.getMySubscriptions(user.id)
        _state.update { it.copy(subscriptions = updatedSubs) }
        return AuthResult(true, "파티에 참여했어요!")
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

    private fun setLoggedInUserFromSeed(user: User) {
        val subscriptions = seedData.subscriptions.filter { subscription ->
            subscription.ownerUserId == user.id || user.id in subscription.members
        }
        val users = seedData.users.let { existing ->
            if (existing.any { it.id == user.id }) existing else existing + user
        }
        _state.update {
            it.copy(
                isLoading = false,
                currentUser = user,
                users = users,
                subscriptions = subscriptions,
                selectedCalendarDate = subscriptions.minByOrNull { sub -> sub.billing.nextBillingDate }
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
