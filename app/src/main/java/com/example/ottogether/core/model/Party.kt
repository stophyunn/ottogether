package com.example.ottogether.core.model

import com.google.firebase.Timestamp

/**
 * Firestore document model for a party room that aggregates OTT subscription members.
 */
data class Party(
    val id: String = "",
    val service: String = "",
    val planId: String = "",
    val billingDay: Int = 1,
    val maxMembers: Int = 1,
    val membersCount: Int = 0,
    val ownerUid: String = "",
    val status: PartyStatus = PartyStatus.Recruiting,
    val inviteCode: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

/**
 * Firestore subcollection document for parties/{partyId}/members/{userUid}
 */
data class PartyMember(
    val partyId: String = "",
    val userUid: String = "",
    val role: PartyRole = PartyRole.Member,
    val joinedAt: Timestamp? = null
)

enum class PartyStatus(val value: String) {
    Recruiting("recruiting"),
    Matched("matched"),
    Ended("ended");

    companion object {
        fun from(value: String?): PartyStatus? = values().firstOrNull { it.value == value }
    }
}

enum class PartyRole(val value: String) {
    Owner("owner"),
    Member("member");

    companion object {
        fun from(value: String?): PartyRole? = values().firstOrNull { it.value == value }
    }
}
