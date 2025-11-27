package com.example.ottogether.core.data

import com.example.ottogether.core.model.Plan
import com.example.ottogether.core.model.Provider
import com.example.ottogether.core.model.Subscription
import java.time.LocalDate

interface SubscriptionRepository {
    suspend fun getMySubscriptions(userId: String): List<Subscription>
    suspend fun getRecommendedParties(
        provider: Provider,
        planId: String? = null,
        excludeUserId: String? = null
    ): List<Subscription>
    suspend fun getSubscription(id: String): Subscription
    suspend fun leaveSubscription(id: String, userId: String)
    suspend fun scheduleLeave(id: String, userId: String, leaveDate: LocalDate)
    suspend fun updateNextBillingDate(id: String, nextDate: LocalDate)
    suspend fun transferOwnership(id: String, newOwnerId: String): Subscription?
    suspend fun joinPartyByCode(code: String, userId: String): Subscription?
    suspend fun createHostedSubscription(
        ownerId: String,
        provider: Provider,
        plan: Plan,
        accountMasked: String?,
        loginId: String?,
        passwordMasked: String?,
        firstBillingDate: LocalDate
    ): Subscription
}