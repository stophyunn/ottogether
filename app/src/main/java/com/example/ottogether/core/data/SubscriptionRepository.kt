package com.example.ottogether.core.data

import com.example.ottogether.core.model.Subscription

interface SubscriptionRepository {
    suspend fun getMySubscriptions(userId: String): List<Subscription>
    suspend fun getSubscription(id: String): Subscription
    suspend fun leaveSubscription(id: String, userId: String)
}