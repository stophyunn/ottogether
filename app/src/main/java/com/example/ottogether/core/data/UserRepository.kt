package com.example.ottogether.core.data

import com.example.ottogether.core.model.User

interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun findByEmail(email: String): User?
    suspend fun getUserById(id: String): User?
    suspend fun addUser(user: User)
    suspend fun updateUser(user: User)
}
