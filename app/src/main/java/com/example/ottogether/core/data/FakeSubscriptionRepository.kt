package com.example.ottogether.core.data

import com.example.ottogether.core.model.BillingInfo
import com.example.ottogether.core.model.Plan
import com.example.ottogether.core.model.Provider
import com.example.ottogether.core.model.Subscription
import java.time.LocalDate

import javax.inject.Inject

class FakeSubscriptionRepository @Inject constructor(
    private val seed: SeedData
) : SubscriptionRepository {

    // 자바 8 API 없이 코틀린 표준으로만
    private val subs: MutableMap<String, Subscription> =
        seed.subscriptions.associateBy { it.id }.toMutableMap()

    override suspend fun getMySubscriptions(userId: String): List<Subscription> {
        pruneExpired()
        return subs.values.filter { it.ownerUserId == userId || userId in it.members }
    }

    override suspend fun getRecommendedParties(
        provider: Provider,
        planId: String?,
        excludeUserId: String?
    ): List<Subscription> {
        pruneExpired()
        return subs.values
            .filter { it.provider == provider }
            .filter { planId == null || it.plan.id == planId }
            .filter { excludeUserId == null || (it.ownerUserId != excludeUserId && excludeUserId !in it.members) }
            .filter { it.members.size + 1 < it.plan.maxScreens }
            .sortedByDescending { it.members.size }
    }

    override suspend fun getSubscription(id: String): Subscription {
        pruneExpired()
        return subs[id] ?: error("Subscription($id) not found")
    }

    override suspend fun leaveSubscription(id: String, userId: String) {
        val s = subs[id] ?: return
        subs[id] = s.copy(
            members = s.members.filterNot { it == userId },
            pendingExits = s.pendingExits - userId
        )
    }

    override suspend fun scheduleLeave(id: String, userId: String, leaveDate: LocalDate) {
        val existing = subs[id] ?: return
        if (userId !in existing.members) return
        subs[id] = existing.copy(
            pendingExits = existing.pendingExits + (userId to leaveDate)
        )
    }

    override suspend fun updateNextBillingDate(id: String, nextDate: LocalDate) {
        val existing = subs[id] ?: return
        subs[id] = existing.copy(
            billing = existing.billing.copy(
                cycleDay = nextDate.dayOfMonth,
                nextBillingDate = nextDate
            )
        )
    }

    override suspend fun transferOwnership(id: String, newOwnerId: String): Subscription? {
        val existing = subs[id] ?: return null
        if (newOwnerId !in existing.members) return null
        val updated = existing.copy(
            ownerUserId = newOwnerId,
            members = listOf(existing.ownerUserId) + existing.members.filterNot { it == newOwnerId }
        )
        subs[id] = updated
        return updated
    }

    override suspend fun joinPartyByCode(code: String, userId: String): Subscription? {
        pruneExpired()
        val existing = subs[code] ?: return null
        if (existing.ownerUserId == userId || userId in existing.members) return null
        if (existing.members.size + 1 >= existing.plan.maxScreens) return null
        val updated = existing.copy(members = existing.members + userId)
        subs[code] = updated
        return updated
    }

    override suspend fun deleteSubscription(id: String) {
        subs.remove(id)
    }

    override suspend fun createHostedSubscription(
        ownerId: String,
        provider: Provider,
        plan: Plan,
        accountMasked: String?,
        loginId: String?,
        passwordMasked: String?,
        firstBillingDate: LocalDate
    ): Subscription {
        val id = generateSubscriptionId()
        val subscription = Subscription(
            id = id,
            provider = provider,
            plan = plan,
            ownerUserId = ownerId,
            members = emptyList(),
            billing = BillingInfo(
                accountMasked = accountMasked,
                loginId = loginId,
                passwordMasked = passwordMasked,
                cycleDay = firstBillingDate.dayOfMonth,
                nextBillingDate = firstBillingDate
            ),
            pendingExits = emptyMap()
        )
        subs[id] = subscription
        return subscription
    }

    private fun generateSubscriptionId(): String =
        "party-" + System.currentTimeMillis().toString(16)

    private fun pruneExpired() {
        val today = LocalDate.now()
        subs.keys.toList().forEach { key ->
            val sub = subs[key] ?: return@forEach
            val expiredMembers = sub.pendingExits.filterValues { it <= today }.keys
            if (expiredMembers.isNotEmpty()) {
                subs[key] = sub.copy(
                    members = sub.members.filterNot { it in expiredMembers },
                    pendingExits = sub.pendingExits - expiredMembers
                )
            }
        }
    }
}