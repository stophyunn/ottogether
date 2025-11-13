package com.example.ottogether.core.data

import com.example.ottogether.core.model.User

interface UserRepository {
    fun getUsers(): List<User>
    fun findByEmail(email: String): User?
    fun addUser(user: User)
    fun updateUser(user: User)
}
