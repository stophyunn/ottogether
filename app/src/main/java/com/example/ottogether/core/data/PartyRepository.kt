package com.example.ottogether.core.data

import com.example.ottogether.core.model.Party
import kotlinx.coroutines.flow.Flow

interface PartyRepository {
    fun myParties(userId: String): Flow<List<Party>>
    suspend fun get(partyId: String): Party?
    suspend fun create(
        providerId: String,
        planId: String,
        ownerId: String,
        nextBillingEpochDay: Long
    ): Party

    /** inviteCode 로 참여. 없으면 null */
    suspend fun join(inviteCode: String, userId: String): Party?

    suspend fun updateNextBillingDate(partyId: String, nextBillingEpochDay: Long)
    suspend fun leave(partyId: String, userId: String): Boolean
}