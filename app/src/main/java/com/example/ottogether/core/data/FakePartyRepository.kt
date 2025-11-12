package com.example.ottogether.core.data

import com.example.ottogether.core.model.Party
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FakePartyRepository @Inject constructor() : PartyRepository {

    // in-memory
    private val state = MutableStateFlow(
        listOf(
            Party(
                id = "p1",
                providerId = "netflix",
                planId = "premium",
                ownerId = "u1",
                members = listOf("u1", "u2", "u3", "u4", "u5"),
                inviteCode = "NX${Random.nextInt(100000, 999999)}",
                nextBillingDate = LocalDate.now().plusDays(3)
            ),
            Party(
                id = "p2",
                providerId = "tving",
                planId = "standard",
                ownerId = "u1",
                members = listOf("u1", "u3"),
                inviteCode = "TV${Random.nextInt(100000, 999999)}",
                nextBillingDate = LocalDate.now().plusDays(10)
            )
        )
    )

    override fun myParties(userId: String): Flow<List<Party>> =
        state.map { list -> list.filter { userId in it.members || it.ownerId == userId } }

    override suspend fun get(partyId: String): Party? = state.value.firstOrNull { it.id == partyId }

    override suspend fun create(
        providerId: String,
        planId: String,
        ownerId: String,
        nextBillingEpochDay: Long
    ): Party {
        val party = Party(
            id = UUID.randomUUID().toString(),
            providerId = providerId,
            planId = planId,
            ownerId = ownerId,
            members = listOf(ownerId),
            inviteCode = providerId.take(2).uppercase() + Random.nextInt(100000, 999999),
            nextBillingDate = LocalDate.ofEpochDay(nextBillingEpochDay)
        )
        state.value = state.value + party
        return party
    }

    override suspend fun join(inviteCode: String, userId: String): Party? {
        val target = state.value.firstOrNull { it.inviteCode.equals(inviteCode, ignoreCase = true) }
            ?: return null
        if (userId in target.members) return target
        val updated = target.copy(members = target.members + userId)
        state.value = state.value.map { if (it.id == target.id) updated else it }
        return updated
    }

    override suspend fun updateNextBillingDate(partyId: String, nextBillingEpochDay: Long) {
        state.value.firstOrNull { it.id == partyId }?.let { p ->
            val upd = p.copy(nextBillingDate = LocalDate.ofEpochDay(nextBillingEpochDay))
            state.value = state.value.map { if (it.id == p.id) upd else it }
        }
    }

    override suspend fun leave(partyId: String, userId: String): Boolean {
        val target = state.value.firstOrNull { it.id == partyId } ?: return false
        val newMembers = target.members.filterNot { it == userId }
        val updated =
            if (newMembers.isEmpty()) null
            else target.copy(members = newMembers)
        state.value =
            if (updated == null) state.value.filterNot { it.id == partyId }
            else state.value.map { if (it.id == partyId) updated else it }
        return true
    }
}