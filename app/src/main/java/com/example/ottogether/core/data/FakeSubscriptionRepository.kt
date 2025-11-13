package com.example.ottogether.core.data

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
        return subs.values.filter { it.ownerUserId == userId || userId in it.members }
    }

    override suspend fun getSubscription(id: String): Subscription {
        return subs[id] ?: error("Subscription($id) not found")
    }

    override suspend fun leaveSubscription(id: String, userId: String) {
        val s = subs[id] ?: return
        subs[id] = s.copy(members = s.members.filterNot { it == userId })
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
        val existing = subs[code] ?: return null
        if (existing.ownerUserId == userId || userId in existing.members) return null
        if (existing.members.size + 1 >= existing.plan.maxScreens) return null
        val updated = existing.copy(members = existing.members + userId)
        subs[code] = updated
        return updated
    }
}