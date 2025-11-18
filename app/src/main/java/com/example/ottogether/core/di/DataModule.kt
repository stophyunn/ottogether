package com.example.ottogether.core.di

import com.example.ottogether.core.data.FirebaseUserRepository
import com.example.ottogether.core.data.FirestoreSubscriptionRepository
import com.example.ottogether.core.data.SubscriptionRepository
import com.example.ottogether.core.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        impl: FirestoreSubscriptionRepository
    ): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: FirebaseUserRepository
    ): UserRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    }
}