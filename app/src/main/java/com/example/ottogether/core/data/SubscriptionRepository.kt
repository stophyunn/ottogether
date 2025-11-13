package com.example.ottogether.core.data

import com.example.ottogether.core.model.Subscription
import java.time.LocalDate

interface SubscriptionRepository {
    suspend fun getMySubscriptions(userId: String): List<Subscription>
    suspend fun getSubscription(id: String): Subscription
    suspend fun leaveSubscription(id: String, userId: String)
    suspend fun updateNextBillingDate(id: String, nextDate: LocalDate)
    suspend fun transferOwnership(id: String, newOwnerId: String): Subscription?
    suspend fun joinPartyByCode(code: String, userId: String): Subscription?
}