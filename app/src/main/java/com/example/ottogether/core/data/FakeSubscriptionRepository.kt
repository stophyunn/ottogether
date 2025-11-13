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
}