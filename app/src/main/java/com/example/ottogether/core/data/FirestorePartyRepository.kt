package com.example.ottogether.core.data

import com.example.ottogether.core.model.Party
import com.example.ottogether.core.model.PartyJoinResult
import com.example.ottogether.core.model.PartyMatchResult
import com.example.ottogether.core.model.PartyRole
import com.example.ottogether.core.model.PartyStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class FirestorePartyRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : PartyRepository {

    private val parties get() = firestore.collection("parties")

    /**
     * Find a recruiting party with matching criteria and try to join it.
     * If none exists, ask UI to create a new party.
     */
    override suspend fun autoMatchOrAskCreate(
        service: String,
        planId: String,
        billingDay: Int,
        currentUserUid: String
    ): PartyMatchResult {
        return try {
            val candidate = parties
                .whereEqualTo("service", service)
                .whereEqualTo("planId", planId)
                .whereEqualTo("billingDay", billingDay)
                .whereEqualTo("status", PartyStatus.Recruiting.value)
                .orderBy("createdAt")
                .limit(1)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.reference

            if (candidate == null) {
                PartyMatchResult.NeedCreateNewParty(service, planId, billingDay)
            } else {
                when (val outcome = tryJoinParty(candidate, currentUserUid, PartyRole.Member)) {
                    is JoinOutcome.Success -> PartyMatchResult.Joined(outcome.party)
                    JoinOutcome.NotRecruiting, JoinOutcome.Full, JoinOutcome.NotFound ->
                        PartyMatchResult.NeedCreateNewParty(service, planId, billingDay)
                    is JoinOutcome.AlreadyMember -> PartyMatchResult.Joined(outcome.party)
                    is JoinOutcome.Error -> PartyMatchResult.Error(outcome.message)
                }
            }
        } catch (e: Exception) {
            PartyMatchResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Join a party by its document id or invite code.
     */
    override suspend fun joinByInvite(
        partyIdOrCode: String,
        currentUserUid: String
    ): PartyJoinResult {
        return try {
            val partyRef = findPartyRefByIdOrCode(partyIdOrCode)
                ?: return PartyJoinResult.NotFound("Party not found")

            return when (val outcome = tryJoinParty(partyRef, currentUserUid, PartyRole.Member)) {
                is JoinOutcome.Success -> PartyJoinResult.Joined(outcome.party)
                JoinOutcome.NotRecruiting -> PartyJoinResult.NotRecruiting("Party is not recruiting")
                JoinOutcome.Full -> PartyJoinResult.Full("Party is already full")
                is JoinOutcome.AlreadyMember -> PartyJoinResult.Joined(outcome.party)
                JoinOutcome.NotFound -> PartyJoinResult.NotFound("Party not found")
                is JoinOutcome.Error -> PartyJoinResult.Error(outcome.message)
            }
        } catch (e: Exception) {
            PartyJoinResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Explicitly create a new party as the owner when auto-match could not find a room.
     */
    override suspend fun createPartyAsOwner(
        service: String,
        planId: String,
        billingDay: Int,
        maxMembers: Int,
        currentUserUid: String
    ): Party? {
        return try {
            val docRef = parties.document()
            val now = Timestamp.now()
            val entity = FirestorePartyEntity(
                service = service,
                planId = planId,
                billingDay = billingDay,
                maxMembers = maxMembers,
                membersCount = 1,
                ownerUid = currentUserUid,
                status = PartyStatus.Recruiting.value,
                inviteCode = docRef.id,
                createdAt = now,
                updatedAt = now
            )
            val member = FirestorePartyMemberEntity(
                partyId = docRef.id,
                userUid = currentUserUid,
                role = PartyRole.Owner.value,
                joinedAt = now
            )

            firestore.runTransaction { tx ->
                tx.set(docRef, entity.toMap())
                tx.set(docRef.collection("members").document(currentUserUid), member.toMap())
                entity
            }.await().toDomain(docRef.id)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun findPartyRefByIdOrCode(partyIdOrCode: String): DocumentReference? {
        val direct = parties.document(partyIdOrCode).get().await()
        if (direct.exists()) return direct.reference

        val byInvite = parties
            .whereEqualTo("inviteCode", partyIdOrCode)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
        return byInvite?.reference
    }

    private suspend fun tryJoinParty(
        partyRef: DocumentReference,
        currentUserUid: String,
        role: PartyRole
    ): JoinOutcome {
        return try {
            firestore.runTransaction { tx ->
                val snapshot = tx.get(partyRef)
                if (!snapshot.exists()) return@runTransaction JoinOutcome.NotFound

                val entity = snapshot.toPartyEntity() ?: return@runTransaction JoinOutcome.NotFound
                if (entity.status != PartyStatus.Recruiting.value) return@runTransaction JoinOutcome.NotRecruiting
                if (entity.membersCount >= entity.maxMembers) return@runTransaction JoinOutcome.Full

                val memberRef = partyRef.collection("members").document(currentUserUid)
                val existingMember = tx.get(memberRef)
                val now = Timestamp.now()
                if (existingMember.exists()) {
                    return@runTransaction JoinOutcome.AlreadyMember(entity.toDomain(partyRef.id, now))
                }

                val updatedCount = entity.membersCount + 1
                val newStatus = if (updatedCount >= entity.maxMembers) PartyStatus.Matched else PartyStatus.Recruiting
                val updatedEntity = entity.copy(
                    membersCount = updatedCount,
                    status = newStatus.value,
                    updatedAt = now
                )

                val member = FirestorePartyMemberEntity(
                    partyId = partyRef.id,
                    userUid = currentUserUid,
                    role = role.value,
                    joinedAt = now
                )

                tx.set(memberRef, member.toMap())
                tx.set(partyRef, updatedEntity.toMap(), SetOptions.merge())

                JoinOutcome.Success(updatedEntity.toDomain(partyRef.id))
            }.await()
        } catch (e: Exception) {
            JoinOutcome.Error(e.message ?: "Unknown error")
        }
    }
}

private data class FirestorePartyEntity(
    val service: String = "",
    val planId: String = "",
    val billingDay: Int = 1,
    val maxMembers: Int = 1,
    val membersCount: Int = 0,
    val ownerUid: String = "",
    val status: String = PartyStatus.Recruiting.value,
    val inviteCode: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "service" to service,
        "planId" to planId,
        "billingDay" to billingDay,
        "maxMembers" to maxMembers,
        "membersCount" to membersCount,
        "ownerUid" to ownerUid,
        "status" to status,
        "inviteCode" to inviteCode,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    fun toDomain(id: String, fallbackUpdatedAt: Timestamp? = null): Party = Party(
        id = id,
        service = service,
        planId = planId,
        billingDay = billingDay,
        maxMembers = maxMembers,
        membersCount = membersCount,
        ownerUid = ownerUid,
        status = PartyStatus.from(status) ?: PartyStatus.Recruiting,
        inviteCode = inviteCode.ifEmpty { id },
        createdAt = createdAt,
        updatedAt = updatedAt ?: fallbackUpdatedAt
    )
}

private data class FirestorePartyMemberEntity(
    val partyId: String = "",
    val userUid: String = "",
    val role: String = PartyRole.Member.value,
    val joinedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "partyId" to partyId,
        "userUid" to userUid,
        "role" to role,
        "joinedAt" to joinedAt
    )
}

private sealed class JoinOutcome {
    data class Success(val party: Party) : JoinOutcome()
    object NotRecruiting : JoinOutcome()
    object Full : JoinOutcome()
    data class AlreadyMember(val party: Party) : JoinOutcome()
    object NotFound : JoinOutcome()
    data class Error(val message: String) : JoinOutcome()
}

private fun com.google.firebase.firestore.DocumentSnapshot.toPartyEntity(): FirestorePartyEntity? {
    val service = getString("service") ?: return null
    val planId = getString("planId") ?: return null
    val billingDay = (getLong("billingDay") ?: return null).toInt()
    val maxMembers = (getLong("maxMembers") ?: return null).toInt()
    val membersCount = (getLong("membersCount") ?: 0L).toInt()
    val ownerUid = getString("ownerUid") ?: return null
    val status = getString("status") ?: PartyStatus.Recruiting.value
    val inviteCode = getString("inviteCode") ?: ""
    val createdAt = getTimestamp("createdAt")
    val updatedAt = getTimestamp("updatedAt")

    return FirestorePartyEntity(
        service = service,
        planId = planId,
        billingDay = billingDay,
        maxMembers = maxMembers,
        membersCount = membersCount,
        ownerUid = ownerUid,
        status = status,
        inviteCode = inviteCode,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
