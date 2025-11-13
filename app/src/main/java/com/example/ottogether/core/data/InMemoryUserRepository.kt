package com.example.ottogether.core.data

import com.example.ottogether.core.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryUserRepository @Inject constructor(seedData: SeedData) : UserRepository {

    private val users = mutableListOf<User>().apply { addAll(seedData.users) }

    override fun getUsers(): List<User> = users.toList()

    override fun findByEmail(email: String): User? =
        users.firstOrNull { it.email?.equals(email, ignoreCase = true) == true }

    override fun addUser(user: User) {
        users.removeAll { it.email?.equals(user.email, ignoreCase = true) == true }
        users.add(user)
    }
}
