package com.example.ottogether.core.data

import com.example.ottogether.core.model.BillingInfo
import com.example.ottogether.core.model.Plan
import com.example.ottogether.core.model.Provider
import com.example.ottogether.core.model.Subscription
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSubscriptionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val seedData: SeedData
) : SubscriptionRepository {

    private val seeded = AtomicBoolean(false)
    private val collection get() = firestore.collection("subscriptions")

    override suspend fun getMySubscriptions(userId: String): List<Subscription> {
        ensureSeedSubscriptions()
        return try {
            val owned = collection.whereEqualTo("ownerUserId", userId).get().await()
            val member = collection.whereArrayContains("members", userId).get().await()
            (owned.documents + member.documents)
                .associateBy({ it.id }, { it.toDomain(seedData) })
                .values
                .filterNotNull()
                .sortedBy { it.billing.nextBillingDate }
        } catch (e: Exception) {
            // TODO: log exception
            emptyList()
        }
    }

    override suspend fun getRecommendedParties(
        provider: Provider,
        planId: String?,
        excludeUserId: String?
    ): List<Subscription> {
        ensureSeedSubscriptions()
        return try {
            val snapshot = collection.whereEqualTo("provider", provider.name).get().await()
            snapshot.documents.mapNotNull { doc ->
                val domain = doc.toDomain(seedData) ?: return@mapNotNull null
                if (planId != null && domain.plan.id != planId) return@mapNotNull null
                if (excludeUserId != null && (domain.ownerUserId == excludeUserId || excludeUserId in domain.members)) {
                    return@mapNotNull null
                }
                if (domain.members.size + 1 >= domain.plan.maxScreens) return@mapNotNull null
                domain
            }.sortedByDescending { it.members.size }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getSubscription(id: String): Subscription {
        ensureSeedSubscriptions()
        return try {
            val snapshot = collection.document(id).get().await()
            snapshot.toDomain(seedData) ?: error("Subscription($id) not found")
        } catch (e: Exception) {
            // TODO: log exception
            throw e
        }
    }

    override suspend fun leaveSubscription(id: String, userId: String) {
        ensureSeedSubscriptions()
        try {
            val docRef = collection.document(id)
            firestore.runTransaction { tx ->
                val snapshot = tx.get(docRef)
                val entity = snapshot.toSubscriptionEntity() ?: return@runTransaction null
                val updatedMembers = entity.members.filterNot { it == userId }
                val updatedPending = entity.pendingExits - userId
                tx.update(docRef, mapOf("members" to updatedMembers, "pendingExits" to updatedPending))
            }.await()
        } catch (e: Exception) {
            // TODO: log exception
        }
    }

    override suspend fun scheduleLeave(id: String, userId: String, leaveDate: LocalDate) {
        ensureSeedSubscriptions()
        try {
            val docRef = collection.document(id)
            firestore.runTransaction { tx ->
                val snapshot = tx.get(docRef)
                val entity = snapshot.toSubscriptionEntity() ?: return@runTransaction null
                if (userId !in entity.members) return@runTransaction null
                val updatedPending = entity.pendingExits + (userId to leaveDate.toString())
                tx.update(docRef, mapOf("pendingExits" to updatedPending))
                updatedPending
            }.await()
        } catch (e: Exception) {
            // TODO: log exception
        }
    }

    override suspend fun updateNextBillingDate(id: String, nextDate: LocalDate) {
        ensureSeedSubscriptions()
        try {
            collection.document(id)
                .update(
                    mapOf(
                        "billing.cycleDay" to nextDate.dayOfMonth,
                        "billing.nextBillingDate" to nextDate.toString()
                    )
                )
                .await()
        } catch (e: Exception) {
            // TODO: log exception
        }
    }

    override suspend fun transferOwnership(id: String, newOwnerId: String): Subscription? {
        ensureSeedSubscriptions()
        return try {
            val docRef = collection.document(id)
            val updated = firestore.runTransaction { tx ->
                val snapshot = tx.get(docRef)
                val entity = snapshot.toSubscriptionEntity() ?: return@runTransaction null
                if (newOwnerId !in entity.members) return@runTransaction null
                val updatedMembers =
                    listOf(entity.ownerUserId) + entity.members.filterNot { it == newOwnerId }
                val updatedEntity = entity.copy(ownerUserId = newOwnerId, members = updatedMembers)
                tx.set(docRef, updatedEntity.toMap())
                updatedEntity
            }.await()
            updated?.toDomain(seedData, id)
        } catch (e: Exception) {
            // TODO: log exception
            null
        }
    }

    override suspend fun joinPartyByCode(code: String, userId: String): Subscription? {
        ensureSeedSubscriptions()
        return try {
            val docRef = collection.document(code)
            val updated = firestore.runTransaction { tx ->
                val snapshot = tx.get(docRef)
                val entity = snapshot.toSubscriptionEntity() ?: return@runTransaction null
                if (entity.ownerUserId == userId || userId in entity.members) return@runTransaction null
                val provider = entity.provider() ?: return@runTransaction null
                val plan = seedData.plan(provider, entity.planId)
                if (entity.members.size + 1 >= plan.maxScreens) return@runTransaction null
                val newMembers = entity.members + userId
                tx.update(docRef, mapOf("members" to newMembers))
                entity.copy(members = newMembers)
            }.await()
            updated?.toDomain(seedData, code)
        } catch (e: Exception) {
            // TODO: log exception
            null
        }
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
        ensureSeedSubscriptions()
        try {
            val id = generateSubscriptionId()
            val entity = FirestoreSubscriptionEntity(
                provider = provider.name,
                planId = plan.id,
                ownerUserId = ownerId,
                members = emptyList(),
                billing = FirestoreBilling(
                    accountMasked = accountMasked,
                    loginId = loginId,
                    passwordMasked = passwordMasked,
                    cycleDay = firstBillingDate.dayOfMonth,
                    nextBillingDate = firstBillingDate.toString()
                ),
                pendingExits = emptyMap()
            )
            collection.document(id).set(entity.toMap()).await()
            return entity.toDomain(seedData, id)!!
        } catch (e: Exception) {
            // TODO: log exception
            throw e
        }
    }

    private suspend fun ensureSeedSubscriptions() {
        if (seeded.getAndSet(true)) return
        try {
            val snapshot = collection.limit(1).get().await()
            if (!snapshot.isEmpty) return
            seedData.subscriptions.forEach { subscription ->
                val entity = FirestoreSubscriptionEntity(
                    provider = subscription.provider.name,
                    planId = subscription.plan.id,
                    ownerUserId = subscription.ownerUserId,
                    members = subscription.members,
                    billing = FirestoreBilling(
                        accountMasked = subscription.billing.accountMasked,
                        loginId = subscription.billing.loginId,
                        passwordMasked = subscription.billing.passwordMasked,
                        cycleDay = subscription.billing.cycleDay,
                        nextBillingDate = subscription.billing.nextBillingDate.toString()
                    ),
                    pendingExits = subscription.pendingExits.mapValues { it.value.toString() }
                )
                collection.document(subscription.id).set(entity.toMap()).await()
            }
        } catch (e: Exception) {
            // TODO: log exception
        }
    }

    private fun generateSubscriptionId(): String =
        "party-" + System.currentTimeMillis().toString(16)
}

private data class FirestoreSubscriptionEntity(
    val provider: String = "",
    val planId: String = "",
    val ownerUserId: String = "",
    val members: List<String> = emptyList(),
    val billing: FirestoreBilling = FirestoreBilling(),
    val pendingExits: Map<String, String> = emptyMap()
) {
    fun provider(): Provider? = runCatching { Provider.valueOf(provider) }.getOrNull()

    fun toDomain(seedData: SeedData, id: String? = null): Subscription? {
        val providerEnum = provider() ?: return null
        val plan = runCatching { seedData.plan(providerEnum, planId) }.getOrNull() ?: return null
        val billingInfo = BillingInfo(
            accountMasked = billing.accountMasked,
            loginId = billing.loginId,
            passwordMasked = billing.passwordMasked,
            cycleDay = billing.cycleDay,
            nextBillingDate = billing.nextBillingDate?.let { LocalDate.parse(it) } ?: LocalDate.now()
        )
        val exitDates = pendingExits.mapNotNull { (memberId, date) ->
            runCatching { LocalDate.parse(date) }.getOrNull()?.let { memberId to it }
        }.toMap()
        val today = LocalDate.now()
        val activeMembers = members.filterNot { memberId ->
            exitDates[memberId]?.let { it <= today } ?: false
        }
        val activeExits = exitDates.filterValues { it > today }
        return Subscription(
            id = id ?: planId,
            provider = providerEnum,
            plan = plan,
            ownerUserId = ownerUserId,
            members = activeMembers,
            billing = billingInfo,
            pendingExits = activeExits
        )
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "provider" to provider,
        "planId" to planId,
        "ownerUserId" to ownerUserId,
        "members" to members,
        "billing" to mapOf(
            "accountMasked" to billing.accountMasked,
            "loginId" to billing.loginId,
            "passwordMasked" to billing.passwordMasked,
            "cycleDay" to billing.cycleDay,
            "nextBillingDate" to billing.nextBillingDate
        ),
        "pendingExits" to pendingExits
    )
}

private data class FirestoreBilling(
    val accountMasked: String? = null,
    val loginId: String? = null,
    val passwordMasked: String? = null,
    val cycleDay: Int = 1,
    val nextBillingDate: String? = null
)

private fun com.google.firebase.firestore.DocumentSnapshot.toSubscriptionEntity(): FirestoreSubscriptionEntity? {
    val provider = getString("provider") ?: return null
    val planId = getString("planId") ?: return null
    val ownerUserId = getString("ownerUserId") ?: return null
    val members = get("members") as? List<*> ?: emptyList<Any>()
    val billingMap = get("billing") as? Map<*, *> ?: emptyMap<Any, Any>()
    val pendingExitMap = get("pendingExits") as? Map<*, *> ?: emptyMap<Any, Any>()
    val billing = FirestoreBilling(
        accountMasked = billingMap["accountMasked"] as? String,
        loginId = billingMap["loginId"] as? String,
        passwordMasked = billingMap["passwordMasked"] as? String,
        cycleDay = (billingMap["cycleDay"] as? Number)?.toInt() ?: 1,
        nextBillingDate = billingMap["nextBillingDate"] as? String
    )
    return FirestoreSubscriptionEntity(
        provider = provider,
        planId = planId,
        ownerUserId = ownerUserId,
        members = members.filterIsInstance<String>(),
        billing = billing,
        pendingExits = (pendingExitMap as? Map<String, Any>)
            ?.mapValues { it.value.toString() }
            ?: emptyMap()
    )
}

private fun com.google.firebase.firestore.DocumentSnapshot.toDomain(seedData: SeedData): Subscription? =
    toSubscriptionEntity()?.toDomain(seedData, id)
