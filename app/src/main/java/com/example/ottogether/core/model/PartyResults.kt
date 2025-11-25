package com.example.ottogether.core.model

sealed class PartyMatchResult {
    data class Joined(val party: Party) : PartyMatchResult()
    data class NeedCreateNewParty(
        val service: String,
        val planId: String,
        val billingDay: Int
    ) : PartyMatchResult()

    data class Error(val message: String) : PartyMatchResult()
}

sealed class PartyJoinResult {
    data class Joined(val party: Party) : PartyJoinResult()
    data class NotRecruiting(val message: String) : PartyJoinResult()
    data class Full(val message: String) : PartyJoinResult()
    data class NotFound(val message: String) : PartyJoinResult()
    data class Error(val message: String) : PartyJoinResult()
}
