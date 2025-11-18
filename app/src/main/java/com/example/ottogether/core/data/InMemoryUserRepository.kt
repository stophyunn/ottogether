package com.example.ottogether.core.data

import com.example.ottogether.core.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryUserRepository @Inject constructor(seedData: SeedData) : UserRepository {

    private val users = mutableListOf<User>().apply { addAll(seedData.users) }

    override suspend fun getUsers(): List<User> = users.toList()

    override suspend fun findByEmail(email: String): User? =
        users.firstOrNull { it.email?.equals(email, ignoreCase = true) == true }

    override suspend fun getUserById(id: String): User? =
        users.firstOrNull { it.id == id }

    override suspend fun addUser(user: User) {
        users.removeAll { it.email?.equals(user.email, ignoreCase = true) == true }
        users.add(user)
    }

    override suspend fun updateUser(user: User) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index >= 0) {
            users[index] = user
        }
    }
}
