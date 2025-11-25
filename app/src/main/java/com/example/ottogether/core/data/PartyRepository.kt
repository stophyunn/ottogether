package com.example.ottogether.core.data

import com.example.ottogether.core.model.Party
import com.example.ottogether.core.model.PartyJoinResult
import com.example.ottogether.core.model.PartyMatchResult

interface PartyRepository {
    suspend fun autoMatchOrAskCreate(
        service: String,
        planId: String,
        billingDay: Int,
        currentUserUid: String
    ): PartyMatchResult

    suspend fun joinByInvite(
        partyIdOrCode: String,
        currentUserUid: String
    ): PartyJoinResult

    suspend fun createPartyAsOwner(
        service: String,
        planId: String,
        billingDay: Int,
        maxMembers: Int,
        currentUserUid: String
    ): Party?
}
