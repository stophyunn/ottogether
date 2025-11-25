package com.example.ottogether.core.model

// core/model/User.kt
data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val profileImageRes: Int? = null, // drawable res id (임시)
    val profileImageUri: String? = null,
    val password: String? = null,
)