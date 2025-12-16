package com.example.ottogether.core.data

import com.example.ottogether.core.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicBoolean

@Singleton
class FirebaseUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val seedData: SeedData
) : UserRepository {

    private val seeded = AtomicBoolean(false)
    private val collection get() = firestore.collection("users")

    override suspend fun getUsers(): List<User> {
        ensureSeedUsers()
        return try {
            collection.get().await().documents.mapNotNull { it.toDomain() }
        } catch (e: Exception) {
            // TODO: log exception
            emptyList()
        }
    }

    override suspend fun findByEmail(email: String): User? {
        ensureSeedUsers()
        return try {
            collection.whereEqualTo("email", email)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.toDomain()
        } catch (e: Exception) {
            // TODO: log exception
            null
        }
    }

    override suspend fun getUserById(id: String): User? {
        ensureSeedUsers()
        return try {
            collection.document(id).get().await().takeIf { it.exists() }?.toDomain()
        } catch (e: Exception) {
            // TODO: log exception
            null
        }
    }

    override suspend fun addUser(user: User) {
        ensureSeedUsers()
        try {
            collection.document(user.id).set(user.toMap()).await()
        } catch (e: Exception) {
            // TODO: log exception
        }
    }

    override suspend fun updateUser(user: User) {
        ensureSeedUsers()
        try {
            collection.document(user.id).set(user.toMap(), SetOptions.merge()).await()
        } catch (e: Exception) {
            // TODO: log exception
        }
    }

    override suspend fun deleteUser(id: String) {
        ensureSeedUsers()
        try {
            collection.document(id).delete().await()
        } catch (e: Exception) {
            // TODO: log exception
        }
    }

    private suspend fun ensureSeedUsers() {
        seeded.set(true)
    }

    private fun User.toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "email" to email,
        "phone" to phone,
        "accountNumber" to accountNumber,
        "profileImageRes" to profileImageRes,
        "profileImageUri" to profileImageUri,
        "password" to password,
    )

    private fun com.google.firebase.firestore.DocumentSnapshot.toDomain(): User? {
        val name = getString("name") ?: return null
        val email = getString("email")
        val phone = getString("phone")
        val accountNumber = getString("accountNumber")
        val profileImageRes = getLong("profileImageRes")?.toInt()
        val profileImageUri = getString("profileImageUri")
        val password = getString("password")
        return User(
            id = id,
            name = name,
            email = email,
            phone = phone,
            accountNumber = accountNumber,
            profileImageRes = profileImageRes,
            profileImageUri = profileImageUri,
            password = password
        )
    }
}
